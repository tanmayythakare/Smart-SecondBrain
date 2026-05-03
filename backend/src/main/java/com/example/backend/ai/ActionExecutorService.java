package com.example.backend.ai;

import com.example.backend.dto.ai.AiActionDto;
import com.example.backend.model.User;
import com.example.backend.service.NoteService;
import com.example.backend.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActionExecutorService {

    private final GeminiService geminiService;
    private final TaskService taskService;
    private final NoteService noteService;
    private final ObjectMapper objectMapper;

    public AiActionDto extractAction(String message) {
        String prompt = """
            Extract a structured action from this user request: "%s"
            
            Return ONLY a JSON object in this format:
            {
              "type": "CREATE_TASK" | "UPDATE_TASK" | "DELETE_TASK" | "CREATE_NOTE" | "UPDATE_NOTE" | "DELETE_NOTE",
              "data": {
                "id": number (for update/delete),
                "title": "string",
                "content": "string (for notes)",
                "completed": boolean (for task update),
                "priority": "LOW" | "MEDIUM" | "HIGH",
                "status": "TODO" | "IN_PROGRESS" | "DONE",
                "dueDate": "YYYY-MM-DD"
              }
            }
            
            If you cannot determine the action, return {"type": "UNKNOWN", "data": {}}.
            """.formatted(message);

        try {
            String rawJson = geminiService.generate(prompt);
            String cleanJson = cleanJson(rawJson);
            return objectMapper.readValue(cleanJson, AiActionDto.class);
        } catch (Exception e) {
            log.error("Action extraction failed: {}", e.getMessage());
            AiActionDto unknown = new AiActionDto();
            unknown.setType(AiActionDto.ActionType.UNKNOWN);
            return unknown;
        }
    }

    public void executeAction(User user, AiActionDto action) {
        log.info("Executing action: {} for user: {}", action.getType(), user.getUsername());

        try {
            switch (action.getType()) {
                case CREATE_TASK -> {
                    String title = (String) action.getData().get("title");
                    String priority = (String) action.getData().get("priority");
                    String dueDate = (String) action.getData().get("dueDate");
                    taskService.createTask(title, priority, dueDate, user);
                }
                case UPDATE_TASK -> {
                    Object idObj = action.getData().get("id");
                    Long id = idObj instanceof Number ? ((Number) idObj).longValue() : Long.parseLong(idObj.toString());
                    String title = (String) action.getData().get("title");
                    Boolean completed = (Boolean) action.getData().get("completed");
                    String status = (String) action.getData().get("status");
                    String priority = (String) action.getData().get("priority");
                    String dueDate = (String) action.getData().get("dueDate");
                    taskService.updateTask(id, title, completed != null && completed, status, priority, dueDate, user);
                }
                case DELETE_TASK -> {
                    Object idObj = action.getData().get("id");
                    Long id = idObj instanceof Number ? ((Number) idObj).longValue() : Long.parseLong(idObj.toString());
                    taskService.deleteTask(id, user);
                }
                case CREATE_NOTE -> {
                    String title = (String) action.getData().get("title");
                    String content = (String) action.getData().get("content");
                    noteService.createNote(title, content, user);
                }
                case UPDATE_NOTE -> {
                    Object idObj = action.getData().get("id");
                    Long id = idObj instanceof Number ? ((Number) idObj).longValue() : Long.parseLong(idObj.toString());
                    String title = (String) action.getData().get("title");
                    String content = (String) action.getData().get("content");
                    noteService.updateNote(id, title, content, user);
                }
                case DELETE_NOTE -> {
                    Object idObj = action.getData().get("id");
                    Long id = idObj instanceof Number ? ((Number) idObj).longValue() : Long.parseLong(idObj.toString());
                    noteService.deleteNote(id, user);
                }
                default -> log.warn("Unknown action type: {}", action.getType());
            }
        } catch (Exception e) {
            log.error("Execution failed: {}", e.getMessage());
            throw new RuntimeException("Failed to execute action: " + e.getMessage());
        }
    }

    private String cleanJson(String raw) {
        if (raw == null) return "{}";
        String processed = raw.trim();
        if (processed.contains("```json")) {
            processed = processed.substring(processed.indexOf("```json") + 7);
            if (processed.contains("```")) {
                processed = processed.substring(0, processed.lastIndexOf("```"));
            }
        } else if (processed.contains("```")) {
             processed = processed.substring(processed.indexOf("```") + 3);
            if (processed.contains("```")) {
                processed = processed.substring(0, processed.lastIndexOf("```"));
            }
        }
        return processed.trim();
    }
}
