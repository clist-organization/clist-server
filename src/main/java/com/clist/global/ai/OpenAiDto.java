package com.clist.global.ai;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
import java.util.Map;

public class OpenAiDto {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChatRequest {
        private String model;
        private List<Message> messages;

        @JsonProperty("max_tokens")
        private int maxTokens;

        @JsonProperty("response_format")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Map<String, String> responseFormat;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Message {
        private String role;
        private String content;
    }

    @Getter
    @NoArgsConstructor
    public static class ChatResponse {
        private List<Choice> choices;

        @Getter
        @NoArgsConstructor
        public static class Choice {
            private Message message;
        }
    }
}