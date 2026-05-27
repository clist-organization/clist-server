package com.clist.domain.md.service;

import com.clist.domain.feedback.entity.FeedbackSession;
import com.clist.domain.feedback.repository.FeedbackMessageRepository;
import com.clist.domain.feedback.repository.FeedbackSessionRepository;
import com.clist.domain.md.dto.MdDto;
import com.clist.domain.md.entity.MdDocument;
import com.clist.domain.md.repository.MdDocumentRepository;
import com.clist.domain.quiz.entity.QuizSession;
import com.clist.domain.quiz.repository.QuizQuestionRepository;
import com.clist.domain.quiz.repository.QuizSessionRepository;
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
public class MdService {

    private final MdDocumentRepository mdDocumentRepository;
    private final QuizSessionRepository quizSessionRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final FeedbackSessionRepository feedbackSessionRepository;
    private final FeedbackMessageRepository feedbackMessageRepository;
    private final UserRepository userRepository;

    @Transactional
    public MdDto.Response create(MdDto.CreateRequest request) {
        User user = getCurrentUser();

        if (mdDocumentRepository.existsByUserAndTitle(user, request.getTitle())) {
            throw new CustomException(ErrorCode.MD_TITLE_DUPLICATE.getStatus(), ErrorCode.MD_TITLE_DUPLICATE.getMessage());
        }

        MdDocument md = MdDocument.builder()
                .user(user)
                .title(request.getTitle())
                .content(request.getContent())
                .build();

        return new MdDto.Response(mdDocumentRepository.save(md));
    }

    @Transactional(readOnly = true)
    public List<MdDto.Response> getAll() {
        User user = getCurrentUser();
        return mdDocumentRepository.findAllByUser(user)
                .stream()
                .map(MdDto.Response::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public MdDto.Response getByTitle(String title) {
        User user = getCurrentUser();
        MdDocument md = mdDocumentRepository.findByUserAndTitle(user, title)
                .orElseThrow(() -> new CustomException(ErrorCode.MD_NOT_FOUND.getStatus(), ErrorCode.MD_NOT_FOUND.getMessage()));
        return new MdDto.Response(md);
    }

    @Transactional
    public void deleteByTitle(String title) {
        User user = getCurrentUser();

        //이거 잘 되는지 확인필요, 이거 떄문에 app 오류 발생해서
        MdDocument md = mdDocumentRepository.findByUserAndTitle(user, title)
                .orElseThrow(() -> new CustomException(ErrorCode.MD_NOT_FOUND.getStatus(), ErrorCode.MD_NOT_FOUND.getMessage()));

        List<QuizSession> quizSessions = quizSessionRepository.findAllByMdDocument(md);
        for (QuizSession session : quizSessions) {
            quizQuestionRepository.deleteAll(quizQuestionRepository.findAllByQuizSession(session));
        }

        quizSessionRepository.deleteAll(quizSessions);

        List<FeedbackSession> feedbackSessions = feedbackSessionRepository.findAllByMdDocument(md);
        for (FeedbackSession session : feedbackSessions) {
            feedbackMessageRepository.deleteAll(
                    feedbackMessageRepository.findAllByFeedbackSessionOrderByCreatedAtAsc(session)
            );
        }

        feedbackSessionRepository.deleteAll(feedbackSessions);

        mdDocumentRepository.delete(md);
    }

    private User getCurrentUser() {
        UUID userId = SecurityUtil.getCurrentUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND.getStatus(), ErrorCode.USER_NOT_FOUND.getMessage()));
    }
}