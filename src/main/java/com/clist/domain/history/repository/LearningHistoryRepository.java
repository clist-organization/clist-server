package com.clist.domain.history.repository;

import com.clist.domain.history.entity.LearningHistory;
import com.clist.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LearningHistoryRepository extends JpaRepository<LearningHistory, UUID> {
    Optional<LearningHistory> findByUser(User user);
}