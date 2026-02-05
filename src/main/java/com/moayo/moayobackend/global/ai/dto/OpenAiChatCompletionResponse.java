package com.moayo.moayobackend.global.ai.dto;

import java.util.List;

public record OpenAiChatCompletionResponse(
        List<Choice> choices
) {
    public record Choice(Message message) {}
    public record Message(String role, String content) {}
}
