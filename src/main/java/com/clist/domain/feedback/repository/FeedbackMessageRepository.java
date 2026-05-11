package com.clist.domain.feedback.repository;

import com.clist.domain.feedback.entity.FeedbackMessage;
import com.clist.domain.feedback.entity.FeedbackSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FeedbackMessageRepository extends JpaRepository<FeedbackMessage, UUID> {
    List<FeedbackMessage> findAllByFeedbackSessionOrderByCreatedAtAsc(FeedbackSession feedbackSession);
}