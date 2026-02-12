package com.moayo.moayobackend.global.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moayo.moayobackend.global.config.OpenAiProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OpenAiClient {

    private final WebClient openAiWebClient;
    private final OpenAiProperties props;

    private static final ObjectMapper om = new ObjectMapper();

    public String draft(String prompt) {

        if (!props.isEnabled()) {
            throw new IllegalStateException("AI 기능이 비활성화되어 있습니다.");
        }

        if (props.getApiKey() == null || props.getApiKey().isBlank()) {
            throw new IllegalStateException("OPENAI_API_KEY가 설정되지 않았습니다.");
        }

        Map<String, Object> body = Map.of(
                "model", props.getModel(),
                "messages", new Object[]{
                        Map.of("role", "system", "content", "너는 이력서 문구를 정리해주는 도우미야."),
                        Map.of("role", "user", "content", prompt)
                }
        );

        String rawResponse = openAiWebClient.post()
                .uri("/v1/chat/completions")
                .bodyValue(body)
                .exchangeToMono(response ->
                        response.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .flatMap(raw -> {
                                    if (response.statusCode().is2xxSuccessful()) {
                                        return Mono.just(raw);
                                    }

                                    // 여기서 429 포함 모든 실패 응답을 body 그대로 남김
                                    return Mono.error(new IllegalStateException(
                                            "OpenAI 호출 실패: status=" +
                                                    response.statusCode().value() +
                                                    " body=" + raw
                                    ));
                                })
                )
                .timeout(Duration.ofMillis(props.getTimeoutMs()))
                // 429일 때만 2번까지 짧게 재시도
                .retryWhen(
                        Retry.backoff(2, Duration.ofMillis(500))
                                .maxBackoff(Duration.ofSeconds(3))
                                .filter(e -> e.getMessage() != null && e.getMessage().contains("status=429"))
                )
                .block();

        try {
            Map<?, ?> result = om.readValue(rawResponse, Map.class);

            List<Map<String, Object>> choices =
                    (List<Map<String, Object>>) result.get("choices");

            if (choices == null || choices.isEmpty()) return "";

            Map<String, Object> message =
                    (Map<String, Object>) choices.get(0).get("message");

            if (message == null) return "";

            return String.valueOf(message.get("content"));

        } catch (Exception e) {
            throw new IllegalStateException("AI 응답 파싱 실패: " + e.getMessage()
                    + " raw=" + rawResponse);
        }
    }
}
