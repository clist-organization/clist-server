package com.clist.domain.quiz.dto;

import com.clist.domain.quiz.entity.QuizQuestion;
import com.clist.domain.quiz.entity.QuizSession;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class QuizDto {

    @Getter
    @NoArgsConstructor
    public static class SessionCreateRequest {
        private String mdTitle;
    }

    @Getter
    @NoArgsConstructor
    public static class AnswerRequest {
        private String answer;
    }

    @Getter
    public static class SessionResponse {
        private final UUID id;
        private final String mdTitle;
        private final String status;
        private final String summary;
        private final LocalDateTime createdAt;

        public SessionResponse(QuizSession session) {
            this.id = session.getId();
            this.mdTitle = session.getMdDocument().getTitle();
            this.status = session.getStatus();
            this.summary = session.getSummary();
            this.createdAt = session.getCreatedAt();
        }
    }

    @Getter
    public static class QuestionResponse {
        private final UUID id;
        private final String question;
        private final String userAnswer;
        private final Boolean isCorrect;

        public QuestionResponse(QuizQuestion question) {
            this.id = question.getId();
            this.question = question.getQuestion();
            this.userAnswer = question.getUserAnswer();
            this.isCorrect = question.getIsCorrect();
        }
    }

    @Getter
    public static class AnswerResponse {
        private final UUID questionId;
        private final boolean correct;
        private final String correctAnswer;
        private final String nextQuestion;

        public AnswerResponse(UUID questionId, boolean correct, String correctAnswer, String nextQuestion) {
            this.questionId = questionId;
            this.correct = correct;
            this.correctAnswer = correctAnswer;
            this.nextQuestion = nextQuestion;
        }
    }

    @Getter
    public static class SessionDetailResponse {
        private final UUID id;
        private final String mdTitle;
        private final String status;
        private final String summary;
        private final List<QuestionResponse> questions;

        public SessionDetailResponse(QuizSession session, List<QuizQuestion> questions) {
            this.id = session.getId();
            this.mdTitle = session.getMdDocument().getTitle();
            this.status = session.getStatus();
            this.summary = session.getSummary();
            this.questions = questions.stream().map(QuestionResponse::new).toList();
        }
    }
}