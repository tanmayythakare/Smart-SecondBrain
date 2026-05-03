package com.example.backend.ai;

import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class PromptBuilder {

    public String build(String context, String history, String userMessage) {
        LocalDateTime now = LocalDateTime.now();
        String formattedTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        int hour = now.getHour();
        String greeting = hour >= 5 && hour < 12 ? "Good morning"
                        : hour >= 12 && hour < 17 ? "Good afternoon"
                        : hour >= 17 && hour < 21 ? "Good evening"
                        : "Good night";

        return """
            ROLE: Strict Personal AI Assistant (Second Brain)
            CURRENT_TIME: """ + formattedTime + """
            GREETING: """ + greeting + """

            CORE INSTRUCTIONS:
            1. You are a reasoning layer for a "Second Brain" application.
            2. You have access to the user's tasks, notes, and conversation history.
            3. Use the CURRENT_TIME above to answer questions about "today", "tomorrow", "now", etc.
            4. When greeting the user, ALWAYS use the exact GREETING value above. Never derive it yourself.

            RULES:
            1. Respond ONLY in valid JSON.
            2. No conversational preamble or postscript.
            3. Use the provided CONTEXT and HISTORY to answer.
            4. If the user asks for their tasks or notes, search the CONTEXT block.
            5. If info is missing, state it clearly in "message".
            6. ALWAYS include relevant tasks or notes in the "items" array if they exist in context.

            SCHEMA:
            {
              "message": "Concise text response",
              "items": [{"id": 123, "type": "task|note", "title": "Title"}]
            }

            HISTORY:
            """ + history + """

            CONTEXT:
            """ + context + """

            USER QUERY:
            """ + userMessage + """

            ASSISTANT RESPONSE (JSON):
            """;
    }
}
