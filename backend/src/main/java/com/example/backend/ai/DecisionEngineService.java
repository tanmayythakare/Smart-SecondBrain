package com.example.backend.ai;

import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Service;
import java.util.regex.Pattern;

@Service
public class DecisionEngineService {

    public enum QueryType {
        CRUD, ANALYTICAL, CONVERSATIONAL
    }

    private static final Pattern TASK_LIST_PATTERN = Pattern.compile("(?i).*\\b(list|show|get|view)\\b.*\\b(tasks|todos)\\b.*");
    private static final Pattern NOTE_LIST_PATTERN = Pattern.compile("(?i).*\\b(list|show|get|view)\\b.*\\b(notes)\\b.*");
    
    private static final Pattern HYBRID_TASK_PATTERN = Pattern.compile(
        "(?i).*\\b(summarize|summary|overdue|urgent|priorit|analyze|status|progress|which|best|worst|top)\\b.*\\b(task|todo)\\b.*"
    );
    private static final Pattern HYBRID_NOTE_PATTERN = Pattern.compile(
        "(?i).*\\b(summarize|summary|analyze|organize|recent|important|key|main)\\b.*\\b(note|notes)\\b.*"
    );
    private static final Pattern INSIGHTS_PATTERN = Pattern.compile(
        "(?i).*\\b(struggling|pattern|habit|trend|productivity|week|month|often|always|never)\\b.*"
    );
    private static final Pattern ACTION_PATTERN = Pattern.compile(
        "(?i).*\\b(create|add|new|make|delete|remove|destroy|remind)\\b.*\\b(task|todo|note)\\b.*"
    );

    public Decision decide(String message) {
        QueryType type = preClassify(message);

        // 1. NON_AI — Pure deterministic CRUD
        if (TASK_LIST_PATTERN.matcher(message).matches()) {
            return Decision.builder()
                    .routingType(RoutingType.NON_AI)
                    .intent(Intent.TASK_MANAGEMENT)
                    .queryType(type)
                    .reason("Deterministic task list.")
                    .build();
        }
        if (NOTE_LIST_PATTERN.matcher(message).matches()) {
            return Decision.builder()
                    .routingType(RoutingType.NON_AI)
                    .intent(Intent.NOTE_MANAGEMENT)
                    .queryType(type)
                    .reason("Deterministic note list.")
                    .build();
        }

        // 2. HYBRID — DB Fetch + AI Reasoning
        if (HYBRID_TASK_PATTERN.matcher(message).matches()) {
            return Decision.builder()
                    .routingType(RoutingType.HYBRID)
                    .intent(Intent.TASK_MANAGEMENT)
                    .queryType(QueryType.ANALYTICAL)
                    .reason("Hybrid: task data + AI analysis.")
                    .build();
        }
        if (HYBRID_NOTE_PATTERN.matcher(message).matches()) {
            return Decision.builder()
                    .routingType(RoutingType.HYBRID)
                    .intent(Intent.NOTE_MANAGEMENT)
                    .queryType(QueryType.ANALYTICAL)
                    .reason("Hybrid: note data + AI analysis.")
                    .build();
        }
        if (INSIGHTS_PATTERN.matcher(message).matches()) {
            return Decision.builder()
                    .routingType(RoutingType.HYBRID)
                    .intent(Intent.LIFE_INSIGHTS)
                    .queryType(QueryType.ANALYTICAL)
                    .reason("Hybrid: pattern/insight query.")
                    .build();
        }

        // 3. ACTION — Agentic Tool Execution
        if (ACTION_PATTERN.matcher(message).matches()) {
            return Decision.builder()
                    .routingType(RoutingType.ACTION)
                    .intent(message.toLowerCase().contains("note") ? Intent.NOTE_MANAGEMENT : Intent.TASK_MANAGEMENT)
                    .queryType(QueryType.CRUD)
                    .reason("Action: Natural language CRUD request.")
                    .build();
        }

        // 3. AI_REQUIRED — Pure Reasoning or Complex RAG
        return Decision.builder()
                .routingType(RoutingType.AI_REQUIRED)
                .intent(Intent.GENERAL_QUERY)
                .queryType(type)
                .reason("Complex reasoning or open-ended query.")
                .build();
    }

    private QueryType preClassify(String message) {
        String msg = message.toLowerCase();
        // Analytical check must come BEFORE CRUD to catch overlaps like "summarize my tasks"
        if (msg.contains("why") || msg.contains("how") || msg.contains("analyze") 
                || msg.contains("summarize") || msg.contains("summary") 
                || msg.contains("pattern") || msg.contains("trend") 
                || msg.contains("struggling") || msg.contains("overdue")) {
            return QueryType.ANALYTICAL;
        }
        if (msg.contains("show") || msg.contains("list") || msg.contains("get") || msg.contains("view")) {
            return QueryType.CRUD;
        }
        return QueryType.CONVERSATIONAL;
    }

    @Data
    @Builder
    public static class Decision {
        private RoutingType routingType;
        private Intent intent;
        private QueryType queryType;
        private String reason;
    }
}
