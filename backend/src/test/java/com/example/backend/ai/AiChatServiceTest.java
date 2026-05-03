package com.example.backend.ai;

import com.example.backend.model.User;
import com.example.backend.repository.AiAuditLogRepository;
import com.example.backend.repository.ChatMessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class AiChatServiceTest {

    private AiChatService aiChatService;

    @Mock private ContextAssemblyService contextAssemblyService;
    @Mock private ResponseValidationService validationService;
    @Mock private GeminiService geminiService;
    @Mock private DecisionEngineService decisionEngineService;
    @Mock private SafetyService safetyService;
    @Mock private SemanticCacheService cacheService;
    @Mock private EmbeddingService embeddingService;
    @Mock private ActionExecutorService actionExecutorService;
    @Mock private RateLimitingService rateLimitingService;
    @Mock private AiAuditLogRepository auditLogRepository;
    @Mock private ContextBuilderService contextBuilderService;
    @Mock private MemoryService memoryService;
    @Mock private ChatMessageRepository chatMessageRepository;
    @Mock private PromptBuilder promptBuilder;
    
    private ObjectMapper objectMapper = new ObjectMapper();
    private MeterRegistry meterRegistry = new SimpleMeterRegistry();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        aiChatService = new AiChatService(
                contextAssemblyService, validationService, geminiService,
                decisionEngineService, safetyService, cacheService,
                embeddingService, actionExecutorService, rateLimitingService,
                auditLogRepository, contextBuilderService, memoryService,
                chatMessageRepository, objectMapper, meterRegistry, promptBuilder
        );
    }

    @Test
    void contextLoads() {
        assertNotNull(aiChatService);
    }
}
