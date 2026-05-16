package com.example.backend.controller;
import com.example.backend.model.AiFeedback;
import com.example.backend.model.User;
import com.example.backend.repository.AiFeedbackRepository;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai/feedback")
@RequiredArgsConstructor
public class AiFeedbackController {

    private final AiFeedbackRepository feedbackRepository;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> submitFeedback(@RequestBody FeedbackRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        AiFeedback feedback = AiFeedback.builder()
                .user(user)
                .userQuery(request.getUserQuery())
                .assistantMessage(request.getAssistantMessage())
                .isPositive(request.getIsPositive())
                .comment(request.getComment())
                .build();

        feedbackRepository.save(feedback);
        return ResponseEntity.ok().build();
    }

    @lombok.Data
    public static class FeedbackRequest {
        private String userQuery;
        private String assistantMessage;
        private Boolean isPositive;
        private String comment;
    }
}
