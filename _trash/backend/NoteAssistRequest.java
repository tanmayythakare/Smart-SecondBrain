package com.example.backend.dto.ai;

import lombok.Data;

@Data
public class NoteAssistRequest {
    private String content;
    private String instruction; // e.g., "summarize", "polish", "expand"
}
