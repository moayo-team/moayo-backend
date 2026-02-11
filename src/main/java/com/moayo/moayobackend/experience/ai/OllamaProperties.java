package com.moayo.moayobackend.experience.ai;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "ai.ollama")
public class OllamaProperties {
    private String baseUrl;
    private String model;
    private int timeoutMs = 20000;
    private boolean enabled = true;
}
