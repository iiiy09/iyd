package com.iyd.config;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "ai.api")
public class AiApiProperties {
    private LlmConfig llm = new LlmConfig();
    @Data
    public static class LlmConfig {
        private String url = "https://api.deepseek.com/v1/chat/completions";
        private String key = "";
        private String model = "deepseek-chat";
    }
}
