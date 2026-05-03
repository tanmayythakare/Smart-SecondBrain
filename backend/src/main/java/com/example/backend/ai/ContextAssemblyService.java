package com.example.backend.ai;

import com.example.backend.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Specialized service for assembling the AI context from multiple tiers (RAG).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ContextAssemblyService {

    private final ContextBuilderService contextBuilderService;
    private final MemoryService memoryService;
    private final ContextBudgetService contextBudgetService;
    private final PromptBuilder promptBuilder;
    private final SafetyService safetyService;

    public String assembleFullPrompt(User user, String userMessage, List<String> history) {
        // 1. Build Multi-Tier Smart Context (Recent + Keyword + Semantic)
        String context = contextBuilderService.buildSmartContext(user, userMessage);
        
        // 2. Format History (Async-summarized context)
        String historyContext = memoryService.getConversationContext(user.getId(), history);
        
        // 3. Build Wrapped Prompt with Safety Delimiters
        String wrappedMessage = safetyService.wrapInput(userMessage);
        String prompt = promptBuilder.build(context, historyContext, wrappedMessage);
        
        // 4. Apply Token Budgeting
        return contextBudgetService.applyBudget(prompt);
    }
    
    public String assembleRetryPrompt(User user, String userMessage, List<String> history) {
        String fullContext = contextBuilderService.buildFullContext(user);
        String historyContext = String.join("\n", history);
        return promptBuilder.build(fullContext, historyContext, safetyService.wrapInput(userMessage));
    }
}
