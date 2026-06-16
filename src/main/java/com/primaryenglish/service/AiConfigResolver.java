package com.primaryenglish.service;

import com.primaryenglish.config.AiDefaultModelConfig;
import com.primaryenglish.config.AiUserModelSelectionConfig;
import com.primaryenglish.entity.User;
import org.springframework.stereotype.Component;

/**
 * AI 配置解析器
 * 
 * 當 ai.user-model-selection.enabled=false（或未設定）時，
 * 強制所有使用者使用系統預設的 Ollama 模型，不需使用者設定。
 */
@Component
public class AiConfigResolver {

    private final AiDefaultModelConfig defaultConfig;
    private final AiUserModelSelectionConfig userModelSelectionConfig;

   
    public AiConfigResolver(AiDefaultModelConfig defaultConfig,
                            AiUserModelSelectionConfig userModelSelectionConfig) {
        this.defaultConfig = defaultConfig;
        this.userModelSelectionConfig = userModelSelectionConfig;
    }

    /**
     * 解析使用者的 AI 配置。
     * 若系統關閉使用者自選，或使用者未設定任何 AI 配置，
     * 則自動填入系統預設值。
     */
    public User resolve(User user) {
        // 系統強制使用預設模型（不自選）
        if (!userModelSelectionConfig.isEnabled()) {
            user.setApiProvider(defaultConfig.getProvider());
            user.setApiModel(defaultConfig.getModel());
            // Ollama 的 base URL 存在 apiKey 欄位（特殊約定）
            user.setApiKey(defaultConfig.getUrl());
            user.setApiEnabled(true);
            return user;
        }

        // 允許自選，但使用者未設定 → 也給預設值
        if (user.getApiProvider() == null || user.getApiProvider().isBlank()) {
            user.setApiProvider(defaultConfig.getProvider());
            user.setApiModel(defaultConfig.getModel());
            user.setApiKey(defaultConfig.getUrl());
        }

        // 若 API Key 仍為空（例如使用者只選 provider 沒填 key）
        if (user.getApiKey() == null || user.getApiKey().isBlank()) {
            user.setApiKey(defaultConfig.getUrl());
        }

        return user;
    }

    /**
     * 檢查解析後的配置是否有效（足以發出 API 請求）
     */
    public boolean isValid(User user) {
        return user.getApiProvider() != null && !user.getApiProvider().isBlank()
            && Boolean.TRUE.equals(user.getApiEnabled());
    }
}
