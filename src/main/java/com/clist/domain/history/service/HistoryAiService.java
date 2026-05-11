package com.clist.domain.history.service;

import com.clist.domain.history.dto.HistoryDto;
import com.clist.global.ai.OpenAiClient;
import com.clist.global.ai.OpenAiDto;
import com.clist.global.exception.CustomException;
import com.clist.global.exception.ErrorCode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HistoryAiService {

    private final OpenAiClient openAiClient;
    private final ObjectMapper objectMapper;

    public List<HistoryDto.ItemRequest> generateHistoryItems(String quizSummary, String feedbackSummary, String mdTitle) {
        String systemPrompt = """
                You are a learning tracker. Based on quiz and feedback results, extract what the user has learned.
                Respond ONLY with valid JSON in this exact format:
                {"items": [{"name": "framework or library name", "content": "what was learned"}]}
                Do not include any other text.
                """;

        String userContent = """
                MD Title: %s
                Quiz Summary: %s
                Feedback Summary: %s
                """.formatted(mdTitle, quizSummary != null ? quizSummary : "없음", feedbackSummary != null ? feedbackSummary : "없음");

        List<OpenAiDto.Message> messages = List.of(
                OpenAiDto.Message.builder().role("system").content(systemPrompt).build(),
                OpenAiDto.Message.builder().role("user").content(userContent).build()
        );

        String response = openAiClient.chatJson(messages);

        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode itemsNode = root.get("items");
            List<HistoryDto.ItemRequest> items = new ArrayList<>();
            for (JsonNode node : itemsNode) {
                items.add(new HistoryDto.ItemRequest(
                        node.get("name").asText(),
                        node.get("content").asText()
                ));
            }
            return items;
        } catch (Exception e) {
            log.error("Failed to parse history items: {}", response, e);
            throw new CustomException(ErrorCode.AI_REQUEST_FAILED.getStatus(), ErrorCode.AI_REQUEST_FAILED.getMessage());
        }
    }
}