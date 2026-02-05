package com.moayo.moayobackend.global.ai;

import com.moayo.moayobackend.global.config.OpenAiProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OpenAiClient {

    private final WebClient openAiWebClient;
    private final OpenAiProperties props;

    public String draft(String prompt) {
        // 1) 배포에서 key 누락 시 바로 명확한 에러
        if (!props.isEnabled()) {
            throw new IllegalStateException("AI 기능이 비활성화되어 있습니다.");
        }
        if (props.getApiKey() == null || props.getApiKey().isBlank()) {
            throw new IllegalStateException("OPENAI_API_KEY가 설정되지 않았습니다.");
        }

        // 2) Responses API 대신 Chat Completions 형태 예시
        Map<String, Object> body = Map.of(
                "model", props.getModel(),
                "messages", new Object[]{
                        Map.of("role", "system", "content", "너는 이력서 문구를 정리해주는 도우미야."),
                        Map.of("role", "user", "content", prompt)
                }
        );

        return openAiWebClient.post()
                .uri("/v1/chat/completions")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .timeout(Duration.ofMillis(props.getTimeoutMs()))
                .onErrorResume(e -> Mono.error(new IllegalStateException("AI 호출 실패: " + e.getMessage())))
                .map(res -> {
                    var choices = (java.util.List<Map<String, Object>>) res.get("choices");
                    if (choices == null || choices.isEmpty()) return "";
                    var msg = (Map<String, Object>) choices.get(0).get("message");
                    if (msg == null) return "";
                    return String.valueOf(msg.get("content"));
                })
                .block();
    }
}
