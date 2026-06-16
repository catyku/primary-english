package com.primaryenglish.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * AI 預設模型設定
 */
@Component
@ConfigurationProperties(prefix = "ai.default-model")
public class AiDefaultModelConfig {
    
    private String provider = "ollama";
    private String model = "gemma4:31b-cloud";
    private String url = "http://10.0.0.186:11434";

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}
