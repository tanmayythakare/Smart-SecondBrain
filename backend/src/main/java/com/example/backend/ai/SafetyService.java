package com.example.backend.ai;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Arrays;

@Service
public class SafetyService {

    private static final List<String> INJECTION_KEYWORDS = Arrays.asList(
            "ignore previous instructions",
            "ignore all instructions",
            "system prompt",
            "developer mode",
            "override system",
            "you are now",
            "act as a"
    );

    public boolean isSafe(String input) {
        if (input == null) return true;
        String lowerInput = input.toLowerCase();
        return INJECTION_KEYWORDS.stream().noneMatch(lowerInput::contains);
    }

    public String sanitizeInput(String input) {
        if (input == null) return "";
        // Basic cleaning: trim and normalize spacing
        return input.trim().replaceAll("\\s+", " ");
    }

    public String wrapInput(String input) {
        // Enclose user input in delimiters to separate it from instructions
        return String.format("<user_input>\n%s\n</user_input>", input);
    }
}
