package com.example.backend.ai;

import com.example.backend.dto.ai.ChatResponseDto;
import com.example.backend.model.User;
import com.example.backend.repository.NoteRepository;
import com.example.backend.repository.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Specialized service for validating AI outputs, fixing JSON, and guarding against hallucinations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ResponseValidationService {

    private final ObjectMapper objectMapper;
    private final TaskRepository taskRepository;
    private final NoteRepository noteRepository;

    public ChatResponseDto validateAndFix(User user, String rawResponse) throws Exception {
        try {
            ChatResponseDto response = objectMapper.readValue(rawResponse, ChatResponseDto.class);
            sanitizeResponse(user, response);
            return response;
        } catch (Exception e) {
            log.warn("JSON parse failed, attempting repair...");
            String fixed = attemptJsonRepair(rawResponse);
            try {
                ChatResponseDto response = objectMapper.readValue(fixed, ChatResponseDto.class);
                sanitizeResponse(user, response);
                return response;
            } catch (Exception e2) {
                log.error("JSON repair failed. Falling back to plain text extraction.");
                // Level 4 Recovery: Wrap raw as plain message if it's not JSON
                return ChatResponseDto.builder()
                    .message(extractPlainText(rawResponse))
                    .build();
            }
        }
    }

    private String attemptJsonRepair(String raw) {
        if (raw == null) return "{}";
        return raw.trim()
            .replaceAll("```json", "")      // strip markdown fences
            .replaceAll("```", "")
            .replaceAll(",\\s*}", "}")      // trailing comma before }
            .replaceAll(",\\s*]", "]")      // trailing comma before ]
            .trim();
    }

    private String extractPlainText(String raw) {
        if (raw == null) return "I encountered an error processing your request.";
        String trimmed = raw.trim();
        // If AI returned prose instead of JSON, use it directly
        if (!trimmed.startsWith("{")) return trimmed;
        
        // If it starts with { but failed parsing, it might be a partial JSON
        return "I processed your request but encountered a formatting issue in the response.";
    }

    /**
     * Hallucination Guard: Cross-verifies every ID mentioned by the AI against the DB.
     */
    public void sanitizeResponse(User user, ChatResponseDto response) {
        if (response.getItems() == null) {
            response.setItems(new java.util.ArrayList<>());
            return;
        }
        
        response.getItems().removeIf(item -> {
            if ("task".equalsIgnoreCase(item.getType())) {
                return !taskRepository.existsByIdAndUser(item.getId(), user);
            } else if ("note".equalsIgnoreCase(item.getType())) {
                return !noteRepository.existsByIdAndUser(item.getId(), user);
            }
            return true; // Remove unknown types
        });
    }
}
