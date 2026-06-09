package com.primaryenglish.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@Component
public class DataInitializer implements CommandLineRunner {

    private final DataSource dataSource;

    public DataInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) throws Exception {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            // 檢查是否已有資料
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM categories");
            int count = rs.next() ? rs.getInt(1) : 0;
            rs.close();
            
            if (count == 0) {
                // 資料庫為空，執行 data.sql
                ScriptUtils.executeSqlScript(conn, new ClassPathResource("data.sql"));
                System.out.println("✅ 資料初始化完成！");
            } else {
                System.out.println("📦 資料庫已有 " + count + " 個分類，跳過初始化。");
            }
        }
    }
}
