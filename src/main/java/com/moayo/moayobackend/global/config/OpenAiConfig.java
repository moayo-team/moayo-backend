package com.moayo.moayobackend.global.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(OpenAiProperties.class)
public class OpenAiConfig {

    @Bean
    public WebClient openAiWebClient(OpenAiProperties props) {
        // baseUrl이 비어있을 때 대비 (local/배포 모두)
        String baseUrl = (props.getBaseUrl() == null || props.getBaseUrl().isBlank())
                ? "https://api.openai.com"
                : props.getBaseUrl();

        WebClient.Builder builder = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Content-Type", "application/json");

        // enabled=true일 때만 Authorization 붙이기
        if (props.isEnabled() && props.getApiKey() != null && !props.getApiKey().isBlank()) {
            builder.defaultHeader("Authorization", "Bearer " + props.getApiKey());
        }

        return builder.build();
    }
}
