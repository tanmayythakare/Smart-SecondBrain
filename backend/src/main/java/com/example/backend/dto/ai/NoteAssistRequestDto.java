package com.example.backend.dto.ai;

import lombok.Data;

@Data
public class NoteAssistRequestDto {
    private String content;
    private String instruction;
}
