package com.primaryenglish.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Flyway 迁移設定
 */
@Component
@ConfigurationProperties(prefix = "flyway")
public class FlywayMigrationConfig {
    
    private boolean enabled = true;
    private String locations = "classpath:db/migration";
    private boolean baselineOnMigrate = true;
    private int baselineVersion = 0;

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public String getLocations() { return locations; }
    public void setLocations(String locations) { this.locations = locations; }

    public boolean isBaselineOnMigrate() { return baselineOnMigrate; }
    public void setBaselineOnMigrate(boolean baselineOnMigrate) { this.baselineOnMigrate = baselineOnMigrate; }

    public int getBaselineVersion() { return baselineVersion; }
    public void setBaselineVersion(int baselineVersion) { this.baselineVersion = baselineVersion; }
}
