package com.clist.domain.quiz.service;

import com.clist.domain.quiz.entity.QuizQuestion;
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
public class QuizAiService {

    private final OpenAiClient openAiClient;
    private final ObjectMapper objectMapper;

    public List<QuizAiService.QuizItem> generateQuestions(String mdContent, int count) {
        String systemPrompt = """
                You are a quiz generator. Given markdown content, generate %d quiz questions.
                Respond ONLY with valid JSON in this exact format:
                {"questions": [{"question": "...", "answer": "..."}]}
                Do not include any other text.
                """.formatted(count);

        List<OpenAiDto.Message> messages = List.of(
                OpenAiDto.Message.builder().role("system").content(systemPrompt).build(),
                OpenAiDto.Message.builder().role("user").content(mdContent).build()
        );

        String response = openAiClient.chatJson(messages);

        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode questionsNode = root.get("questions");
            List<QuizItem> items = new ArrayList<>();
            for (JsonNode node : questionsNode) {
                items.add(new QuizItem(
                        node.get("question").asText(),
                        node.get("answer").asText()
                ));
            }
            return items;
        } catch (Exception e) {
            log.error("Failed to parse quiz questions: {}", response, e);
            throw new CustomException(ErrorCode.AI_REQUEST_FAILED.getStatus(), ErrorCode.AI_REQUEST_FAILED.getMessage());
        }
    }

    public boolean gradeAnswer(String question, String correctAnswer, String userAnswer) {
        String systemPrompt = """
                You are a quiz grader. Given a question, the correct answer, and the user's answer,
                determine if the user's answer is correct (semantically equivalent is acceptable).
                Respond ONLY with valid JSON: {"correct": true} or {"correct": false}
                """;

        String userContent = """
                Question: %s
                Correct Answer: %s
                User Answer: %s
                """.formatted(question, correctAnswer, userAnswer);

        List<OpenAiDto.Message> messages = List.of(
                OpenAiDto.Message.builder().role("system").content(systemPrompt).build(),
                OpenAiDto.Message.builder().role("user").content(userContent).build()
        );

        String response = openAiClient.chatJson(messages);

        try {
            JsonNode root = objectMapper.readTree(response);
            return root.get("correct").asBoolean();
        } catch (Exception e) {
            log.error("Failed to parse grade result: {}", response, e);
            return false;
        }
    }

    public String generateSummary(String mdTitle, List<QuizQuestion> questions) {
        String systemPrompt = """
                You are a learning assistant. Summarize the quiz results and provide feedback.
                Respond in Korean. Keep it concise (under 200 characters).
                """;

        long correct = questions.stream().filter(q -> Boolean.TRUE.equals(q.getIsCorrect())).count();
        String userContent = """
                MD Title: %s
                Total Questions: %d
                Correct: %d
                Wrong: %d
                """.formatted(mdTitle, questions.size(), correct, questions.size() - correct);

        List<OpenAiDto.Message> messages = List.of(
                OpenAiDto.Message.builder().role("system").content(systemPrompt).build(),
                OpenAiDto.Message.builder().role("user").content(userContent).build()
        );

        return openAiClient.chat(messages);
    }

    public record QuizItem(String question, String answer) {}
}