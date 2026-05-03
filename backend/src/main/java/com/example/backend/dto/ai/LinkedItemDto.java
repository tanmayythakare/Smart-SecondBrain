package com.example.backend.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkedItemDto {
    private Long id;
    private String type; // "task" or "note"
    private String title;
    private LocalDate dueDate;
}
