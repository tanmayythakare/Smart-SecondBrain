package com.example.backend.ai;

import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ContextBudgetService {

    // Upgraded from 4,000 to 32,000 as per Gemini 1.5 Flash capabilities
    private static final int MAX_TOKENS = 32000; 
    
    // TODO: Replace with real tokenization (tiktoken or Gemini's countTokens API)
    private static final int CHARS_PER_TOKEN = 4; 

    /**
     * Estimates token count and trims content if it exceeds the budget.
     * Strategy: Prioritize instructions (Top) and recent query/data (Bottom).
     */
    public String applyBudget(String context) {
        if (context == null) return "";
        
        int estimatedTokens = estimateTokens(context);
        
        if (estimatedTokens > MAX_TOKENS) {
            log.warn("Context budget exceeded ({} tokens). Applying priority-based truncation...", estimatedTokens);
            
            int allowedChars = MAX_TOKENS * CHARS_PER_TOKEN;
            
            // Allocation: 25% for instructions (Head), 75% for recent context/query (Tail)
            // This is better than a middle-cut because instructions are critical for LLM behavior.
            int headChars = allowedChars / 4;
            int tailChars = allowedChars - headChars;

            if (context.length() <= allowedChars) return context;

            return context.substring(0, headChars) + 
                   "\n\n... [TRUNCATED MIDDLE TO STAY WITHIN BUDGET] ...\n\n" + 
                   context.substring(context.length() - tailChars);
        }
        
        return context;
    }

    public int estimateTokens(String text) {
        if (text == null) return 0;
        return text.length() / CHARS_PER_TOKEN;
    }
}
