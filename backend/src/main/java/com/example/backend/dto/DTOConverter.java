package com.example.backend.dto;

import com.example.backend.model.Note;
import com.example.backend.model.Task;
import com.example.backend.model.TaskStatus;

public class DTOConverter {

    public static NoteDTO toDTO(Note note) {
        NoteDTO dto = new NoteDTO();
        dto.setId(note.getId());
        dto.setTitle(note.getTitle());
        dto.setContent(note.getContent());
        dto.setCreatedAt(note.getCreatedAt());
        dto.setUpdatedAt(note.getUpdatedAt());
        return dto;
    }

    public static TaskDTO toDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setCompleted(task.getStatus() == TaskStatus.DONE);
        dto.setStatus(task.getStatus() != null ? task.getStatus().name() : "TODO");
        dto.setPriority(task.getPriority() != null ? task.getPriority().name() : "MEDIUM");
        dto.setDueDate(task.getDueDate());
        dto.setCreatedAt(task.getCreatedAt());
        return dto;
    }
}
