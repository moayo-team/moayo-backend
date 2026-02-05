package com.moayo.moayobackend.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "ai.openai")
public class OpenAiProperties {
    private boolean enabled;
    private String baseUrl;
    private String apiKey;
    private String model;
    private int timeoutMs;
}
