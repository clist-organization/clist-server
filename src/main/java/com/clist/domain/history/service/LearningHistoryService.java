package com.clist.domain.history.service;

import com.clist.domain.history.dto.HistoryDto;
import com.clist.domain.history.entity.LearningHistory;
import com.clist.domain.history.entity.LearningHistoryList;
import com.clist.domain.history.repository.LearningHistoryListRepository;
import com.clist.domain.history.repository.LearningHistoryRepository;
import com.clist.domain.user.entity.User;
import com.clist.domain.user.repository.UserRepository;
import com.clist.global.exception.CustomException;
import com.clist.global.exception.ErrorCode;
import com.clist.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LearningHistoryService {

    private final LearningHistoryRepository learningHistoryRepository;
    private final LearningHistoryListRepository learningHistoryListRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public HistoryDto.Response getHistory() {
        User user = getCurrentUser();

        LearningHistory history = learningHistoryRepository.findByUser(user)
                .orElseThrow(() -> new CustomException(ErrorCode.HISTORY_NOT_FOUND.getStatus(), ErrorCode.HISTORY_NOT_FOUND.getMessage()));

        List<LearningHistoryList> items = learningHistoryListRepository.findAllByLearningHistory(history);

        return new HistoryDto.Response(history, items);
    }

    @Transactional
    public HistoryDto.Response update(HistoryDto.UpdateRequest request) {
        User user = getCurrentUser();

        LearningHistory history = learningHistoryRepository.findByUser(user)
                .orElseGet(() -> learningHistoryRepository.save(
                        LearningHistory.builder().user(user).build()
                ));

        // 기존 항목 삭제 후 새로 추가
        List<LearningHistoryList> existingItems = learningHistoryListRepository.findAllByLearningHistory(history);
        learningHistoryListRepository.deleteAll(existingItems);

        List<LearningHistoryList> newItems = request.getItems().stream()
                .map(item -> LearningHistoryList.builder()
                        .learningHistory(history)
                        .name(item.getName())
                        .content(item.getContent())
                        .build())
                .toList();

        learningHistoryListRepository.saveAll(newItems);

        return new HistoryDto.Response(history, newItems);
    }

    /**
     * 퀴즈/피드백 완료 후 내부에서 자동 호출되는 업데이트 메서드
     */
    @Transactional
    public void appendItems(User user, List<HistoryDto.ItemRequest> items) {
        LearningHistory history = learningHistoryRepository.findByUser(user)
                .orElseGet(() -> learningHistoryRepository.save(
                        LearningHistory.builder().user(user).build()
                ));

        List<LearningHistoryList> newItems = items.stream()
                .map(item -> LearningHistoryList.builder()
                        .learningHistory(history)
                        .name(item.getName())
                        .content(item.getContent())
                        .build())
                .toList();

        learningHistoryListRepository.saveAll(newItems);
    }

    private User getCurrentUser() {
        UUID userId = SecurityUtil.getCurrentUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND.getStatus(), ErrorCode.USER_NOT_FOUND.getMessage()));
    }
}