package com.primaryenglish.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * AI 模型選擇設定
 * 
 * ai.user-model-selection.enabled=true  → 允許使用者在個人資料中自訂 AI 模型
 * ai.user-model-selection.enabled=false → 強制使用系統預設模型，使用者不需設定
 */
@Component
@ConfigurationProperties(prefix = "ai.user-model-selection")
public class AiUserModelSelectionConfig {
    
    /** 是否允許使用者自行選擇 AI 模型，預設 true */
    private boolean enabled = true;

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
