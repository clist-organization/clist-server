package com.clist.global.ai;

import com.clist.global.exception.CustomException;
import com.clist.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenAiClient {

    private final WebClient webClient;

    @Value("${openai.api-key}")
    private String apiKey;

    @Value("${openai.model:gpt-4o-mini}")
    private String model;

    public String chat(List<OpenAiDto.Message> messages) {
        return chat(messages, 2000, null);
    }

    public String chatJson(List<OpenAiDto.Message> messages) {
        return chat(messages, 2000, Map.of("type", "json_object"));
    }

    private String chat(List<OpenAiDto.Message> messages, int maxTokens, Map<String, String> responseFormat) {
        try {
            OpenAiDto.ChatRequest request = OpenAiDto.ChatRequest.builder()
                    .model(model)
                    .messages(messages)
                    .maxTokens(maxTokens)
                    .responseFormat(responseFormat)
                    .build();

            OpenAiDto.ChatResponse response = webClient.post()
                    .uri("https://api.openai.com/v1/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(OpenAiDto.ChatResponse.class)
                    .block();

            if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
                throw new CustomException(ErrorCode.AI_REQUEST_FAILED.getStatus(), ErrorCode.AI_REQUEST_FAILED.getMessage());
            }

            return response.getChoices().get(0).getMessage().getContent();

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("OpenAI API call failed: ", e);
            throw new CustomException(ErrorCode.AI_REQUEST_FAILED.getStatus(), ErrorCode.AI_REQUEST_FAILED.getMessage());
        }
    }
}