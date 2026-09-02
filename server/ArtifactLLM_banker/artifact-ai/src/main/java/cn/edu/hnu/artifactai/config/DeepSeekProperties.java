package cn.edu.hnu.artifactai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "ai.deepseek")
public class DeepSeekProperties {
    /**
     * API Key
     */
    private String apiKey = "sk-6f015483467b42c899233139feacfe11";

    /**
     * Base URL (e.g., https://api.deepseek.com)
     */
    private String baseUrl = "https://api.deepseek.com";

    /**
     * Model name (e.g., deepseek-chat)
     */
    private String model = "deepseek-reasoner";
    
    /**
     * Max tokens
     */
    private Integer maxTokens = 2048;
    
    /**
     * Temperature
     */
    private Double temperature = 0.7;
}
