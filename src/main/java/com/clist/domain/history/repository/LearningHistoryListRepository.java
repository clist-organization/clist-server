package com.clist.domain.history.repository;

import com.clist.domain.history.entity.LearningHistory;
import com.clist.domain.history.entity.LearningHistoryList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LearningHistoryListRepository extends JpaRepository<LearningHistoryList, UUID> {
    List<LearningHistoryList> findAllByLearningHistory(LearningHistory learningHistory);
}