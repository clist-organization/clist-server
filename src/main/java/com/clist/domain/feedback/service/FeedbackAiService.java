package com.clist.domain.feedback.service;

import com.clist.domain.feedback.entity.FeedbackMessage;
import com.clist.global.ai.OpenAiClient;
import com.clist.global.ai.OpenAiDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackAiService {

    private final OpenAiClient openAiClient;

    public String generateReply(String mdContent, List<FeedbackMessage> history, String userMessage) {
        String systemPrompt = """
                You are a learning assistant that helps users understand the content of a markdown document.
                Analyze the content and provide helpful, concise feedback in Korean.
                The user can ask questions about the content, request explanations, or ask for quizzes.
                """;

        List<OpenAiDto.Message> messages = new ArrayList<>();
        messages.add(OpenAiDto.Message.builder()
                .role("system")
                .content(systemPrompt + "\n\nDocument Content:\n" + mdContent)
                .build());

        for (FeedbackMessage msg : history) {
            messages.add(OpenAiDto.Message.builder()
                    .role(msg.getRole())
                    .content(msg.getMessage())
                    .build());
        }

        messages.add(OpenAiDto.Message.builder()
                .role("user")
                .content(userMessage)
                .build());

        return openAiClient.chat(messages);
    }

    public String generateSummary(String mdTitle, List<FeedbackMessage> messages) {
        String systemPrompt = """
                You are a learning assistant. Summarize the feedback session in Korean.
                Keep it concise (under 200 characters).
                """;

        StringBuilder conversation = new StringBuilder();
        conversation.append("MD Title: ").append(mdTitle).append("\n\n");
        for (FeedbackMessage msg : messages) {
            conversation.append("[").append(msg.getRole()).append("]: ").append(msg.getMessage()).append("\n");
        }

        List<OpenAiDto.Message> aiMessages = List.of(
                OpenAiDto.Message.builder().role("system").content(systemPrompt).build(),
                OpenAiDto.Message.builder().role("user").content(conversation.toString()).build()
        );

        return openAiClient.chat(aiMessages);
    }
}