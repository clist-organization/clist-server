package com.clist.domain.quiz.service;

import com.clist.domain.md.entity.MdDocument;
import com.clist.domain.md.repository.MdDocumentRepository;
import com.clist.domain.quiz.dto.QuizDto;
import com.clist.domain.quiz.entity.QuizQuestion;
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
public class QuizService {

    private static final int QUIZ_COUNT = 5;

    private final QuizSessionRepository quizSessionRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final MdDocumentRepository mdDocumentRepository;
    private final UserRepository userRepository;
    private final QuizAiService quizAiService;

    @Transactional
    public QuizDto.SessionDetailResponse createSession(QuizDto.SessionCreateRequest request) {
        User user = getCurrentUser();

        quizSessionRepository.findActiveSessionByUser(user).ifPresent(s -> {
            throw new CustomException(ErrorCode.QUIZ_ACTIVE_SESSION_EXISTS.getStatus(), ErrorCode.QUIZ_ACTIVE_SESSION_EXISTS.getMessage());
        });

        MdDocument md = mdDocumentRepository.findByUserAndTitle(user, request.getMdTitle())
                .orElseThrow(() -> new CustomException(ErrorCode.MD_NOT_FOUND.getStatus(), ErrorCode.MD_NOT_FOUND.getMessage()));

        QuizSession session = QuizSession.builder()
                .user(user)
                .mdDocument(md)
                .status("ACTIVE")
                .build();
        quizSessionRepository.save(session);

        List<QuizAiService.QuizItem> items = quizAiService.generateQuestions(md.getContent(), QUIZ_COUNT);

        List<QuizQuestion> questions = items.stream()
                .map(item -> QuizQuestion.builder()
                        .quizSession(session)
                        .question(item.question())
                        .answer(item.answer())
                        .build())
                .toList();
        quizQuestionRepository.saveAll(questions);

        return new QuizDto.SessionDetailResponse(session, questions);
    }

    @Transactional(readOnly = true)
    public List<QuizDto.SessionResponse> getAllSessions() {
        User user = getCurrentUser();
        return quizSessionRepository.findAllByUser(user)
                .stream()
                .map(QuizDto.SessionResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<QuizDto.SessionResponse> getSessionsByMdTitle(String mdTitle) {
        User user = getCurrentUser();
        return quizSessionRepository.findAllByUserAndMdDocument_Title(user, mdTitle)
                .stream()
                .map(QuizDto.SessionResponse::new)
                .toList();
    }

    @Transactional
    public QuizDto.AnswerResponse submitAnswer(QuizDto.AnswerRequest request) {
        User user = getCurrentUser();

        QuizSession session = quizSessionRepository.findActiveSessionByUser(user)
                .orElseThrow(() -> new CustomException(ErrorCode.QUIZ_NO_ACTIVE_SESSION.getStatus(), ErrorCode.QUIZ_NO_ACTIVE_SESSION.getMessage()));

        QuizQuestion question = quizQuestionRepository.findFirstUnansweredBySession(session)
                .orElseThrow(() -> new CustomException(ErrorCode.QUIZ_ALL_ANSWERED.getStatus(), ErrorCode.QUIZ_ALL_ANSWERED.getMessage()));

        boolean isCorrect = quizAiService.gradeAnswer(question.getQuestion(), question.getAnswer(), request.getAnswer());
        question.submitAnswer(request.getAnswer(), isCorrect);
        quizQuestionRepository.save(question);

        // 다음 미답변 질문 확인
        String nextQuestion = quizQuestionRepository.findFirstUnansweredBySession(session)
                .map(QuizQuestion::getQuestion)
                .orElse(null);

        // 모든 질문 답변 완료 시 세션 종료
        if (nextQuestion == null) {
            List<QuizQuestion> allQuestions = quizQuestionRepository.findAllByQuizSession(session);
            String summary = quizAiService.generateSummary(session.getMdDocument().getTitle(), allQuestions);
            session.close(summary);
            quizSessionRepository.save(session);
        }

        return new QuizDto.AnswerResponse(question.getId(), isCorrect, question.getAnswer(), nextQuestion);
    }

    @Transactional
    public void deleteSession(UUID sessionId) {
        User user = getCurrentUser();
        QuizSession session = quizSessionRepository.findById(sessionId)
                .orElseThrow(() -> new CustomException(ErrorCode.QUIZ_SESSION_NOT_FOUND.getStatus(), ErrorCode.QUIZ_SESSION_NOT_FOUND.getMessage()));

        if (!session.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.FORBIDDEN.getStatus(), ErrorCode.FORBIDDEN.getMessage());
        }

        quizSessionRepository.delete(session);
    }

    private User getCurrentUser() {
        UUID userId = SecurityUtil.getCurrentUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND.getStatus(), ErrorCode.USER_NOT_FOUND.getMessage()));
    }
}