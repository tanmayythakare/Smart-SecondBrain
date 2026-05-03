package com.example.backend.ai;

import com.example.backend.model.Note;
import com.example.backend.model.Task;
import com.example.backend.repository.NoteRepository;
import com.example.backend.repository.TaskRepository;
import com.example.backend.event.NoteEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiSearchService {

    private final EmbeddingService embeddingService;
    private final NoteRepository noteRepository;
    private final TaskRepository taskRepository;

    @Async
    @EventListener
    public void handleNoteEvent(NoteEvent event) {
        log.info("Received NoteEvent for ID: {} Type: {}", event.getNoteId(), event.getType());
        generateAndSaveNoteEmbedding(event.getNoteId());
    }

    @Async
    public void generateAndSaveNoteEmbedding(Long noteId) {
        log.info("Generating embedding for note ID: {}", noteId);
        noteRepository.findById(noteId).ifPresent(note -> {
            String text = note.getTitle() + " " + note.getContent();
            List<Double> embedding = embeddingService.getEmbedding(text);
            if (!embedding.isEmpty()) {
                note.setEmbedding(embedding);
                noteRepository.save(note);
                log.info("Successfully saved embedding for note ID: {}", noteId);
            }
        });
    }

    @Async
    public void generateAndSaveTaskEmbedding(Long taskId) {
        log.info("Generating embedding for task ID: {}", taskId);
        taskRepository.findById(taskId).ifPresent(task -> {
            String text = task.getTitle();
            List<Double> embedding = embeddingService.getEmbedding(text);
            if (!embedding.isEmpty()) {
                task.setEmbedding(embedding);
                taskRepository.save(task);
                log.info("Successfully saved embedding for task ID: {}", taskId);
            }
        });
    }
}
