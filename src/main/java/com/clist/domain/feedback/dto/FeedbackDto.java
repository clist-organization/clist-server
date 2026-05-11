package com.clist.domain.feedback.dto;

import com.clist.domain.feedback.entity.FeedbackMessage;
import com.clist.domain.feedback.entity.FeedbackSession;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class FeedbackDto {

    @Getter
    @NoArgsConstructor
    public static class SessionCreateRequest {
        private String mdTitle;
    }

    @Getter
    @NoArgsConstructor
    public static class MessageRequest {
        private String message;
    }

    @Getter
    public static class SessionResponse {
        private final UUID id;
        private final String mdTitle;
        private final String status;
        private final String summary;
        private final LocalDateTime createdAt;

        public SessionResponse(FeedbackSession session) {
            this.id = session.getId();
            this.mdTitle = session.getMdDocument().getTitle();
            this.status = session.getStatus();
            this.summary = session.getSummary();
            this.createdAt = session.getCreatedAt();
        }
    }

    @Getter
    public static class MessageResponse {
        private final UUID id;
        private final String role;
        private final String message;
        private final LocalDateTime createdAt;

        public MessageResponse(FeedbackMessage msg) {
            this.id = msg.getId();
            this.role = msg.getRole();
            this.message = msg.getMessage();
            this.createdAt = msg.getCreatedAt();
        }
    }

    @Getter
    public static class SessionDetailResponse {
        private final UUID id;
        private final String mdTitle;
        private final String status;
        private final String summary;
        private final List<MessageResponse> messages;

        public SessionDetailResponse(FeedbackSession session, List<FeedbackMessage> messages) {
            this.id = session.getId();
            this.mdTitle = session.getMdDocument().getTitle();
            this.status = session.getStatus();
            this.summary = session.getSummary();
            this.messages = messages.stream().map(MessageResponse::new).toList();
        }
    }
}