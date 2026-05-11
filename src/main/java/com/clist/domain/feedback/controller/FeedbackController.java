package com.clist.domain.feedback.controller;

import com.clist.domain.feedback.dto.FeedbackDto;
import com.clist.domain.feedback.service.FeedbackService;
import com.clist.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping("/session")
    public ResponseEntity<ApiResponse<FeedbackDto.SessionResponse>> createSession(@RequestBody FeedbackDto.SessionCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(feedbackService.createSession(request)));
    }

    @PostMapping("/message")
    public ResponseEntity<ApiResponse<FeedbackDto.MessageResponse>> sendMessage(@RequestBody FeedbackDto.MessageRequest request) {
        return ResponseEntity.ok(ApiResponse.success(feedbackService.sendMessage(request)));
    }

    @GetMapping("/session/{id}")
    public ResponseEntity<ApiResponse<FeedbackDto.SessionDetailResponse>> getSession(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(feedbackService.getSession(id)));
    }

    @DeleteMapping("/session/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSession(@PathVariable UUID id) {
        feedbackService.deleteSession(id);
        return ResponseEntity.ok(ApiResponse.success());
    }
}