package com.primaryenglish.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SQLite 兼容的数据库迁移工具
 * 
 * 功能类似 Flyway，专为 SQLite 设计：
 * 1. 扫描 db/migration/V{version}__{description}.sql 脚本
 * 2. 记录已执行版本到 flyway_schema_history 表
 * 3. 按版本号顺序执行未执行的迁移
 * 4. 支持 baseline（标记已有数据库为基线版本）
 */
@Component
public class SQLiteFlywayMigration {

    private static final Logger logger = LoggerFactory.getLogger(SQLiteFlywayMigration.class);
    private static final Pattern MIGRATION_PATTERN = Pattern.compile(
        "V(\\d+)__(.+)\\.sql$"
    );
    private static final String HISTORY_TABLE = "flyway_schema_history";

    private final DataSource dataSource;
    private final FlywayMigrationConfig config;

    public SQLiteFlywayMigration(DataSource dataSource, FlywayMigrationConfig config) {
        this.dataSource = dataSource;
        this.config = config;
    }

    /**
     * 执行数据库迁移
     */
    public void migrate() {
        if (!config.isEnabled()) {
            logger.info("SQLiteFlywayMigration 已禁用，跳过迁移");
            return;
        }

        logger.info("开始 SQLite 数据库迁移...");

        try {
            // 1. 创建历史记录表
            createHistoryTable();

            // 2. 如果需要，设置 baseline
            if (config.isBaselineOnMigrate() && isBaselineNeeded()) {
                baseline();
            }

            // 3. 扫描并执行迁移脚本
            List<MigrationScript> scripts = scanMigrationScripts();
            int applied = 0;

            for (MigrationScript script : scripts) {
                if (!isVersionApplied(script.version)) {
                    applyMigration(script);
                    applied++;
                }
            }

            if (applied > 0) {
                logger.info("数据库迁移完成，共执行 {} 个脚本", applied);
            } else {
                logger.info("数据库已是最新版本，无需迁移");
            }

        } catch (Exception e) {
            logger.error("数据库迁移失败: {}", e.getMessage(), e);
            throw new RuntimeException("数据库迁移失败", e);
        }
    }

    /**
     * 创建迁移历史记录表
     */
    private void createHistoryTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS %s (
                installed_rank INTEGER PRIMARY KEY AUTOINCREMENT,
                version VARCHAR(50),
                description VARCHAR(200) NOT NULL,
                type VARCHAR(20) NOT NULL DEFAULT 'SQL',
                script VARCHAR(1000) NOT NULL,
                checksum INTEGER,
                installed_by VARCHAR(100) NOT NULL DEFAULT '',
                installed_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                execution_time INTEGER,
                success BOOLEAN NOT NULL DEFAULT TRUE
            )
            """.formatted(HISTORY_TABLE);

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    /**
     * 检查是否需要设置 baseline（已有数据库但没有迁移记录）
     */
    private boolean isBaselineNeeded() throws SQLException {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT COUNT(*) FROM " + HISTORY_TABLE)) {
            return rs.next() && rs.getInt(1) == 0;
        }
    }

    /**
     * 设置 baseline：将当前数据库标记为某个初始版本
     */
    private void baseline() throws SQLException {
        int baselineVersion = config.getBaselineVersion();
        String desc = "<< Flyway Baseline >>";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 """
                INSERT INTO %s (version, description, type, script, installed_on, success)
                VALUES (?, ?, 'BASELINE', ?, datetime('now'), TRUE)
                """.formatted(HISTORY_TABLE))) {
            ps.setString(1, String.valueOf(baselineVersion));
            ps.setString(2, desc);
            ps.setString(3, desc);
            ps.executeUpdate();
            logger.info("已设置 baseline 版本: {}", baselineVersion);
        }
    }

    /**
     * 扫描迁移脚本文件
     */
    private List<MigrationScript> scanMigrationScripts() throws Exception {
        List<MigrationScript> scripts = new ArrayList<>();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        String location = config.getLocations()
            .replace("classpath:", "")
            .replace(".", "/");
        String pattern = "classpath:" + location + "/V*__*.sql";

        logger.debug("扫描迁移脚本: {}", pattern);
        Resource[] resources = resolver.getResources(pattern);

        for (Resource resource : resources) {
            String filename = resource.getFilename();
            if (filename == null) continue;

            Matcher matcher = MIGRATION_PATTERN.matcher(filename);
            if (matcher.find()) {
                int version = Integer.parseInt(matcher.group(1));
                String description = matcher.group(2).replace("_", " ");
                scripts.add(new MigrationScript(version, description, filename, resource));
            }
        }

        scripts.sort((a, b) -> Integer.compare(a.version, b.version));
        logger.debug("扫描到 {} 个迁移脚本", scripts.size());
        return scripts;
    }

    /**
     * 检查某个版本是否已经执行过
     */
    private boolean isVersionApplied(int version) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT COUNT(*) FROM " + HISTORY_TABLE + " WHERE version = ? AND success = TRUE")) {
            ps.setString(1, String.valueOf(version));
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    /**
     * 执行单个迁移脚本
     */
    private void applyMigration(MigrationScript script) throws Exception {
        logger.info("正在执行迁移: V{}__{}", script.version, script.description);

        String sql = readSqlFile(script.resource);
        long startTime = System.currentTimeMillis();

        try (Connection conn = dataSource.getConnection()) {
            // SQLite 不支持多语句 PreparedStatement，逐条执行
            conn.setAutoCommit(false);

            try {
                for (String statement : splitStatements(sql)) {
                    String trimmed = statement.trim();
                    if (trimmed.isEmpty() || trimmed.startsWith("--")) {
                        continue;
                    }
                    try (Statement stmt = conn.createStatement()) {
                        stmt.execute(trimmed);
                    }
                }

                // 记录到历史表
                recordMigration(conn, script);
                conn.commit();

                long duration = System.currentTimeMillis() - startTime;
                logger.info("迁移 V{}__{} 执行完成 ({} ms)", script.version, script.description, duration);

            } catch (Exception e) {
                conn.rollback();
                recordFailedMigration(conn, script, e.getMessage());
                throw e;
            }
        }
    }

    /**
     * 读取 SQL 文件内容
     */
    private String readSqlFile(Resource resource) throws Exception {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * 将 SQL 字符串拆分为独立的语句
     */
    private List<String> splitStatements(String sql) {
        List<String> statements = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        for (String line : sql.split("\n")) {
            String trimmed = line.trim();

            // 跳过注释和空行
            if (trimmed.isEmpty() || trimmed.startsWith("--")) {
                continue;
            }

            current.append(line).append("\n");

            // 以分号结尾的为完整语句
            if (trimmed.endsWith(";")) {
                statements.add(current.toString());
                current = new StringBuilder();
            }
        }

        // 处理最后没有分号的语句
        if (!current.toString().trim().isEmpty()) {
            statements.add(current.toString());
        }

        return statements;
    }

    /**
     * 记录成功执行的迁移
     */
    private void recordMigration(Connection conn, MigrationScript script) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("""
            INSERT INTO %s (version, description, type, script, installed_on, success)
            VALUES (?, ?, 'SQL', ?, datetime('now'), TRUE)
            """.formatted(HISTORY_TABLE))) {
            ps.setString(1, String.valueOf(script.version));
            ps.setString(2, script.description);
            ps.setString(3, script.filename);
            ps.executeUpdate();
        }
    }

    /**
     * 记录失败的迁移
     */
    private void recordFailedMigration(Connection conn, MigrationScript script, String errorMsg) {
        try (PreparedStatement ps = conn.prepareStatement("""
            INSERT INTO %s (version, description, type, script, installed_on, success)
            VALUES (?, ?, 'SQL', ?, datetime('now'), FALSE)
            """.formatted(HISTORY_TABLE))) {
            ps.setString(1, String.valueOf(script.version));
            ps.setString(2, script.description + " [FAILED: " + errorMsg.substring(0, Math.min(100, errorMsg.length())) + "]");
            ps.setString(3, script.filename);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.warn("记录失败迁移时出错: {}", e.getMessage());
        }
    }

    // ========== 内部类 ==========

    private static class MigrationScript {
        final int version;
        final String description;
        final String filename;
        final Resource resource;

        MigrationScript(int version, String description, String filename, Resource resource) {
            this.version = version;
            this.description = version + " " + description;
            this.filename = filename;
            this.resource = resource;
        }
    }
}
