package com.example.backend.ai;

import com.example.backend.dto.ai.ChatResponseDto;
import com.example.backend.dto.ai.AiActionDto;
import com.example.backend.model.AiAuditLog;
import com.example.backend.model.ChatMessage;
import com.example.backend.model.User;
import com.example.backend.repository.AiAuditLogRepository;
import com.example.backend.repository.ChatMessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import com.example.backend.dto.ai.SidebarDataDto;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import com.example.backend.repository.NoteRepository;
import com.example.backend.repository.TaskRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import com.example.backend.model.Note;
import com.example.backend.model.Task;
import com.example.backend.model.TaskStatus;

import java.util.List;
import java.util.stream.Collectors;
import com.example.backend.ai.RoutingType;
import com.example.backend.ai.Intent;

@Service
@Slf4j
public class AiChatService {

    private final ContextAssemblyService contextAssemblyService;
    private final ResponseValidationService validationService;
    private final GeminiService geminiService;
    private final DecisionEngineService decisionEngineService;
    private final SafetyService safetyService;
    private final SemanticCacheService cacheService;
    private final EmbeddingService embeddingService;
    private final ActionExecutorService actionExecutorService;
    private final RateLimitingService rateLimitingService;
    private final AiAuditLogRepository auditLogRepository;
    private final ContextBuilderService contextBuilderService;
    private final MemoryService memoryService;
    private final ChatMessageRepository chatMessageRepository;
    private final ObjectMapper objectMapper;
    private final Timer processTimer;
    private final Timer aiGenerationTimer;
    private final PromptBuilder promptBuilder;

    public AiChatService(ContextAssemblyService contextAssemblyService, ResponseValidationService validationService,
                        GeminiService geminiService, DecisionEngineService decisionEngineService,
                        SafetyService safetyService, SemanticCacheService cacheService,
                        EmbeddingService embeddingService, ActionExecutorService actionExecutorService,
                        RateLimitingService rateLimitingService, AiAuditLogRepository auditLogRepository,
                        ContextBuilderService contextBuilderService, MemoryService memoryService,
                        ChatMessageRepository chatMessageRepository, ObjectMapper objectMapper,
                        MeterRegistry meterRegistry, PromptBuilder promptBuilder) {
        this.contextAssemblyService = contextAssemblyService;
        this.validationService = validationService;
        this.geminiService = geminiService;
        this.decisionEngineService = decisionEngineService;
        this.safetyService = safetyService;
        this.cacheService = cacheService;
        this.embeddingService = embeddingService;
        this.actionExecutorService = actionExecutorService;
        this.rateLimitingService = rateLimitingService;
        this.auditLogRepository = auditLogRepository;
        this.contextBuilderService = contextBuilderService;
        this.memoryService = memoryService;
        this.chatMessageRepository = chatMessageRepository;
        this.objectMapper = objectMapper;
        this.promptBuilder = promptBuilder;
        this.processTimer = Timer.builder("ai.chat.process.time")
                .description("Time taken to process a chat message")
                .register(meterRegistry);
        this.aiGenerationTimer = Timer.builder("ai.chat.generation.time")
                .description("Time taken by Gemini for generation")
                .register(meterRegistry);
    }

    @CircuitBreaker(name = "geminiService", fallbackMethod = "handleAiFallback")
    public ChatResponseDto processMessage(User user, String userMessage) {
        if (!rateLimitingService.isAllowed(user.getId())) {
            return ChatResponseDto.builder()
                    .message("You've reached the AI request limit (10/min). Please take a short breath and try again.")
                    .build();
        }
        long startTime = System.currentTimeMillis();
        String status = "SUCCESS";
        String errorMsg = null;
        RoutingType routingType = RoutingType.AI_REQUIRED;
        Intent intent = Intent.GENERAL_QUERY;

        try {
            if (!safetyService.isSafe(userMessage)) {
                return ChatResponseDto.builder().message("Safety violation detected.").build();
            }
            String sanitized = safetyService.sanitizeInput(userMessage);

            DecisionEngineService.Decision decision = decisionEngineService.decide(sanitized);
            routingType = decision.getRoutingType();
            
            if (routingType == RoutingType.NON_AI) {
                intent = decision.getIntent();
                return handleDeterministicRouting(user, decision);
            }
            
            if (routingType == RoutingType.HYBRID) {
                intent = decision.getIntent();
                return handleHybridRouting(user, sanitized, decision);
            }
            
            if (routingType == RoutingType.ACTION) {
                intent = decision.getIntent();
                return handleActionRouting(user, sanitized, decision);
            }

            List<Double> queryEmbedding = embeddingService.getEmbedding(sanitized);
            String cached = cacheService.get(user.getId(), sanitized, queryEmbedding);
            if (cached != null) {
                status = "CACHE_HIT";
                return validationService.validateAndFix(user, cached);
            }

            List<String> history = getHistoryFromDb(user);
            String prompt = contextAssemblyService.assembleFullPrompt(user, sanitized, history);
            
            long aiStart = System.currentTimeMillis();
            ChatResponseDto response = executeWithRecovery(user, sanitized, history, prompt, queryEmbedding, 1);
            aiGenerationTimer.record(System.currentTimeMillis() - aiStart, java.util.concurrent.TimeUnit.MILLISECONDS);
            
            updateHistory(user, sanitized, response.getMessage());
            return response;

        } catch (Exception e) {
            status = "FAILURE";
            errorMsg = e.getMessage();
            throw new RuntimeException(e);
        } finally {
            long totalLatency = System.currentTimeMillis() - startTime;
            processTimer.record(totalLatency, java.util.concurrent.TimeUnit.MILLISECONDS);
            saveAuditLog(user, userMessage, intent, routingType, totalLatency, status, errorMsg);
        }
    }

    public Flux<String> streamProcessMessage(User user, String userMessage) {
        if (!rateLimitingService.isAllowed(user.getId())) {
            return Flux.just("{\"message\": \"Rate limit exceeded. Please wait a minute.\"}");
        }
        long startTime = System.currentTimeMillis();
        if (!safetyService.isSafe(userMessage)) {
            return Flux.just("{\"message\": \"Safety violation detected.\"}");
        }
        String sanitized = safetyService.sanitizeInput(userMessage);

        DecisionEngineService.Decision decision = decisionEngineService.decide(sanitized);
        
        if (decision.getRoutingType() == RoutingType.NON_AI) {
            ChatResponseDto result = handleDeterministicRouting(user, decision);
            return Flux.just(serializeSafe(result));
        }

        if (decision.getRoutingType() == RoutingType.ACTION) {
            ChatResponseDto result = handleActionRouting(user, sanitized, decision);
            return Flux.just(serializeSafe(result));
        }

        List<Double> queryEmbedding = embeddingService.getEmbedding(sanitized);
        String cached = cacheService.get(user.getId(), sanitized, queryEmbedding);
        if (cached != null) {
            return Flux.just(cached);
        }

        List<String> history = getHistoryFromDb(user);
        
        String prompt = decision.getRoutingType() == RoutingType.HYBRID
            ? buildHybridPrompt(user, sanitized, history, decision)
            : contextAssemblyService.assembleFullPrompt(user, sanitized, history);

        StringBuilder accumulated = new StringBuilder();
        return geminiService.streamGenerate(prompt)
            .filter(chunk -> chunk != null && !chunk.isBlank())
            .doOnNext(accumulated::append)
            .doOnComplete(() -> {
                if (accumulated.length() > 0) {
                    updateHistory(user, userMessage, accumulated.toString());
                }
                long latency = System.currentTimeMillis() - startTime;
                saveAuditLog(user, userMessage, decision.getIntent(), decision.getRoutingType(), latency, "STREAM_SUCCESS", null);
            })
            .onErrorResume(e -> {
                log.error("Stream failed for user {}: {}", user.getId(), e.getMessage());
                long latency = System.currentTimeMillis() - startTime;
                saveAuditLog(user, userMessage, decision.getIntent(), decision.getRoutingType(), latency, "STREAM_FAILURE", e.getMessage());
                return Flux.just("{\"message\": \"Stream interrupted. Please retry.\"}");
            });
    }

    public List<com.example.backend.dto.ai.ChatMessageDto> getHistory(User user) {
        List<ChatMessage> history = chatMessageRepository.findTop10ByUserOrderByTimestampDesc(user);
        java.util.Collections.reverse(history);
        return history.stream()
                .map(m -> com.example.backend.dto.ai.ChatMessageDto.builder()
                        .role(m.getRole().name().toLowerCase())
                        .content(m.getContent())
                        .timestamp(m.getTimestamp())
                        .build())
                .collect(java.util.stream.Collectors.toList());
    }

    private String buildHybridPrompt(User user, String message, List<String> history, DecisionEngineService.Decision decision) {
        String context = switch (decision.getIntent()) {
            case TASK_MANAGEMENT -> contextBuilderService.buildTasksContext(user);
            case NOTE_MANAGEMENT -> contextBuilderService.buildNotesContext(user);
            case LIFE_INSIGHTS    -> contextBuilderService.buildTasksContext(user) + "\n" + contextBuilderService.buildNotesContext(user);
            default              -> contextBuilderService.buildTasksContext(user);
        };
        String historyContext = memoryService.getConversationContext(user.getId(), history);
        return promptBuilder.build(context, historyContext, message);
    }

    private String serializeSafe(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "{\"message\": \"Response serialization failed.\"}";
        }
    }

    private ChatResponseDto executeWithRecovery(User user, String userMessage, List<String> history, String prompt, List<Double> queryEmbedding, int attempt) {
        String raw = geminiService.generate(prompt);
        try {
            return validationService.validateAndFix(user, raw);
        } catch (Exception e) {
            log.error("AI execution error for user {}: {}", user.getId(), e.getMessage());
            if (attempt < 2) {
                log.info("Recovery attempt {} for user {}", attempt, user.getId());
                String retryPrompt = contextAssemblyService.assembleRetryPrompt(user, userMessage, history);
                return executeWithRecovery(user, userMessage, history, retryPrompt, queryEmbedding, attempt + 1);
            }
            return ChatResponseDto.builder().message("I encountered an issue, but here is the raw result: " + raw).build();
        }
    }

    private ChatResponseDto handleHybridRouting(User user, String message, DecisionEngineService.Decision decision) {
        log.info("HYBRID routing | user={} | intent={} | reason={}", user.getId(), decision.getIntent(), decision.getReason());
        
        List<String> history = getHistoryFromDb(user);
        String prompt = buildHybridPrompt(user, message, history, decision);
        
        List<Double> queryEmbedding = embeddingService.getEmbedding(message);
        ChatResponseDto response = executeWithRecovery(user, message, history, prompt, queryEmbedding, 1);
        
        updateHistory(user, message, response.getMessage());
        return response;
    }

    private ChatResponseDto handleActionRouting(User user, String message, DecisionEngineService.Decision decision) {
        AiActionDto action = actionExecutorService.extractAction(message);
        if (action.getType() == AiActionDto.ActionType.UNKNOWN) {
            List<String> history = getHistoryFromDb(user);
            String prompt = contextAssemblyService.assembleFullPrompt(user, message, history);
            return executeWithRecovery(user, message, history, prompt, null, 1);
        }

        String confirmMsg = switch (action.getType()) {
            case CREATE_TASK -> "I can create this task for you: \"" + action.getData().get("title") + "\". Shall I proceed?";
            case UPDATE_TASK -> "I'll update task " + action.getData().get("id") + ". Ready to proceed?";
            case DELETE_TASK -> "I found a task to delete (ID: " + action.getData().get("id") + "). Confirm deletion?";
            case CREATE_NOTE -> "I can save this note: \"" + action.getData().get("title") + "\". Should I add it to your Second Brain?";
            case UPDATE_NOTE -> "I'll update note " + action.getData().get("id") + " with your changes. Confirm?";
            case DELETE_NOTE -> "Are you sure you want to delete note " + action.getData().get("id") + "?";
            default -> "I've prepared an action. Proceed?";
        };

        return ChatResponseDto.builder()
                .message(confirmMsg)
                .action(action)
                .build();
    }

    public ChatResponseDto confirmAction(User user, AiActionDto action) {
        try {
            actionExecutorService.executeAction(user, action);
            String successMsg = switch (action.getType()) {
                case CREATE_TASK -> "Task created successfully.";
                case UPDATE_TASK -> "Task updated.";
                case DELETE_TASK -> "Task deleted.";
                case CREATE_NOTE -> "Note saved.";
                case UPDATE_NOTE -> "Note updated.";
                case DELETE_NOTE -> "Note removed.";
                default -> "Action executed.";
            };
            return ChatResponseDto.builder().message(successMsg).build();
        } catch (Exception e) {
            return ChatResponseDto.builder().message("Failed to execute action: " + e.getMessage()).build();
        }
    }

    public SidebarDataDto getSidebarData(User user) {
        List<Task> tasks = contextBuilderService.getUpcomingTasks(user);
        List<SidebarDataDto.UpcomingTask> upcoming = tasks.stream()
                .map(t -> new SidebarDataDto.UpcomingTask(t.getTitle(), formatDueDate(t)))
                .collect(Collectors.toList());

        String insight = memoryService.getLatestInsight(user.getId());
        if (insight == null || insight.isBlank()) {
            insight = "Focus on completing your top priority tasks today.";
        }

        Note note = contextBuilderService.getLatestNote(user);
        SidebarDataDto.RelatedNote related = note != null 
            ? new SidebarDataDto.RelatedNote(note.getTitle(), formatTimeAgo(note.getUpdatedAt()))
            : new SidebarDataDto.RelatedNote("No notes yet", "N/A");

        return SidebarDataDto.builder()
                .upcomingTasks(upcoming)
                .memoryInsight(insight)
                .relatedNote(related)
                .build();
    }

    private String formatDueDate(Task t) {
        if (t.getDueDate() == null) return "No due date";
        return t.getDueDate().toString();
    }

    private String formatTimeAgo(java.time.LocalDateTime dt) {
        if (dt == null) return "N/A";
        return "recently"; // Simplified
    }

    public ChatResponseDto handleAiFallback(User user, String userMessage, Exception t) {
        log.error("Circuit breaker triggered! Fallback active for user {}", user.getId());
        return ChatResponseDto.builder()
                .message("The AI engine is currently under heavy load. I'm operating in deterministic mode. How can I help with your tasks or notes?")
                .build();
    }

    private ChatResponseDto handleDeterministicRouting(User user, DecisionEngineService.Decision decision) {
        ChatResponseDto response = new ChatResponseDto();
        if (decision.getIntent() == Intent.TASK_MANAGEMENT) {
            response.setMessage("Here are your current tasks:");
            response.setItems(contextBuilderService.getTasksAsItems(user));
        } else {
            response.setMessage("Here are your recent notes:");
            response.setItems(contextBuilderService.getNotesAsItems(user));
        }
        return response;
    }

    private void updateHistory(User user, String userMsg, String assistantMsg) {
        chatMessageRepository.save(new ChatMessage(user, com.example.backend.model.MessageRole.USER, userMsg));
        chatMessageRepository.save(new ChatMessage(user, com.example.backend.model.MessageRole.ASSISTANT, assistantMsg));
    }

    private List<String> getHistoryFromDb(User user) {
        List<ChatMessage> recent = chatMessageRepository.findTop10ByUserOrderByTimestampDesc(user);
        java.util.Collections.reverse(recent);
        return recent.stream()
            .map(m -> (m.getRole() == com.example.backend.model.MessageRole.USER ? "User: " : "Assistant: ") + m.getContent())
            .collect(java.util.stream.Collectors.toList());
    }

    private void saveAuditLog(User user, String query, Intent intent, RoutingType routing, long latency, String status, String error) {
        String safeQuery = (query != null && query.length() > 255) ? query.substring(0, 250) + "..." : query;
        String safeError = (error != null && error.length() > 255) ? error.substring(0, 250) + "..." : error;
        auditLogRepository.save(AiAuditLog.builder()
                .user(user).userQuery(safeQuery).intent(intent != null ? intent.name() : "UNKNOWN")
                .routingType(routing.name()).latencyMs(latency).status(status).errorMessage(safeError).build());
    }

    public String assistNote(User user, String content, String instruction) {
        log.info("AI Note Assist | user={} | instruction={}", user.getId(), instruction);
        String prompt = String.format(
            "You are a professional AI Note Assistant. Below is the content of a user's note and a specific instruction.\n\n" +
            "INSTRUCTION: %s\n\n" +
            "NOTE CONTENT:\n%s\n\n" +
            "Respond ONLY with the revised content, summary, or extracted data as requested. Do not include any introductory phrases like 'Here is the summary' or conversational filler. Your response will be applied directly to the user's note.",
            instruction, content
        );
        
        try {
            return geminiService.generate(prompt);
        } catch (Exception e) {
            log.error("Note assist failed: {}", e.getMessage());
            return "Error: Could not process note assistant request.";
        }
    }
}
