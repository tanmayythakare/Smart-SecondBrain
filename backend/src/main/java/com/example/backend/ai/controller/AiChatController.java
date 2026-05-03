package com.example.backend.ai.controller;

import com.example.backend.ai.AiChatService;
import com.example.backend.dto.ai.ChatRequestDto;
import com.example.backend.dto.ai.ChatResponseDto;
import com.example.backend.dto.ai.AiActionDto;
import com.example.backend.model.User;
import com.example.backend.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import com.example.backend.dto.ai.ChatMessageDto;
import com.example.backend.security.RateLimiter;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.backend.dto.ai.NoteAssistRequestDto;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Slf4j
public class AiChatController {

    private final AiChatService aiChatService;
    private final ExecutorService executor = Executors.newCachedThreadPool();

    private final RateLimiter rateLimiter;

    @PostMapping("/notes/assist")
    public Map<String, String> assistNote(@RequestBody NoteAssistRequestDto request, @AuthenticationPrincipal User user) {
        if (user == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        String result = aiChatService.assistNote(user, request.getContent(), request.getInstruction());
        return Map.of("result", result);
    }

    @GetMapping("/chat/history")
    public List<ChatMessageDto> getHistory(@AuthenticationPrincipal User user) {
        if (user == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        return aiChatService.getHistory(user);
    }

    @PostMapping("/chat")
    public ChatResponseDto chat(@RequestBody ChatRequestDto request, 
                               @AuthenticationPrincipal User user,
                               HttpServletRequest servletRequest) {
        if (user == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        if (!rateLimiter.isAllowed(servletRequest.getRemoteAddr())) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests. Please slow down.");
        }
        return aiChatService.processMessage(user, request.getMessage());
    }

    @PostMapping(value = "/chat/stream")
    public SseEmitter streamChat(@RequestBody ChatRequestDto request, 
                                @AuthenticationPrincipal User user,
                                HttpServletRequest servletRequest) {
        if (user == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        if (!rateLimiter.isAllowed(servletRequest.getRemoteAddr())) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded.");
        }
        
        SseEmitter emitter = new SseEmitter(120000L);
        
        aiChatService.streamProcessMessage(user, request.getMessage())
                .doOnNext(content -> {
                    try {
                        emitter.send(SseEmitter.event()
                                .data(content)
                                .build());
                    } catch (Exception e) {
                        log.warn("Failed to send chunk to emitter: {}", e.getMessage());
                    }
                })
                .doOnComplete(() -> {
                    try {
                        emitter.send(SseEmitter.event().data("[DONE]").build());
                        emitter.complete();
                    } catch (Exception e) {
                        emitter.complete();
                    }
                })
                .doOnError(e -> {
                    log.error("Stream error in controller: {}", e.getMessage());
                    try {
                        emitter.send(SseEmitter.event().comment("Error: " + e.getMessage()).build());
                        emitter.complete();
                    } catch (Exception ignored) {
                        emitter.complete();
                    }
                })
                .subscribe();
        
        return emitter;
    }

    @PostMapping("/chat/confirm")
    public ChatResponseDto confirmAction(@RequestBody AiActionDto action, @AuthenticationPrincipal User user) {
        if (user == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        return aiChatService.confirmAction(user, action);
    }

    @GetMapping("/context/sidebar")
    public com.example.backend.dto.ai.SidebarDataDto getSidebar(@AuthenticationPrincipal User user) {
        if (user == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        return aiChatService.getSidebarData(user);
    }
}
