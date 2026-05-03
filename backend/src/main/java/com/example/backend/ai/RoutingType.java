package com.example.backend.ai;

public enum RoutingType {
    NON_AI,      // Deterministic backend response
    AI_REQUIRED, // Full LLM reasoning
    HYBRID,      // Pre-processed context + LLM
    ACTION       // Agentic action (CRUD)
}
