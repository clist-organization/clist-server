package com.clist.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Auth
    EMAIL_ALREADY_EXISTS(400, "이미 존재하는 이메일입니다."),
    INVALID_PASSWORD(401, "비밀번호가 올바르지 않습니다."),
    USER_NOT_FOUND(404, "사용자를 찾을 수 없습니다."),

    // MD
    MD_NOT_FOUND(404, "MD 문서를 찾을 수 없습니다."),
    MD_TITLE_DUPLICATE(400, "이미 존재하는 MD 제목입니다."),

    // Quiz
    QUIZ_SESSION_NOT_FOUND(404, "퀴즈 세션을 찾을 수 없습니다."),
    QUIZ_ACTIVE_SESSION_EXISTS(400, "이미 활성화된 퀴즈 세션이 존재합니다."),
    QUIZ_NO_ACTIVE_SESSION(404, "활성화된 퀴즈 세션이 없습니다."),
    QUIZ_QUESTION_NOT_FOUND(404, "퀴즈 질문을 찾을 수 없습니다."),
    QUIZ_ALL_ANSWERED(400, "모든 질문에 이미 답변했습니다."),

    // Feedback
    FEEDBACK_SESSION_NOT_FOUND(404, "피드백 세션을 찾을 수 없습니다."),
    FEEDBACK_ACTIVE_SESSION_EXISTS(400, "이미 활성화된 피드백 세션이 존재합니다."),
    FEEDBACK_NO_ACTIVE_SESSION(404, "활성화된 피드백 세션이 없습니다."),

    // History
    HISTORY_NOT_FOUND(404, "학습 이력을 찾을 수 없습니다."),

    // AI
    AI_REQUEST_FAILED(500, "AI 요청 처리 중 오류가 발생했습니다."),

    // Common
    UNAUTHORIZED(401, "인증이 필요합니다."),
    FORBIDDEN(403, "접근 권한이 없습니다."),
    INTERNAL_SERVER_ERROR(500, "서버 내부 오류가 발생했습니다.");

    private final int status;
    private final String message;
}