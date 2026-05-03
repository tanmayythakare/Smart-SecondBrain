package com.example.backend.ai;

import com.example.backend.model.Note;
import com.example.backend.model.Task;
import com.example.backend.model.TaskStatus;
import com.example.backend.model.User;
import com.example.backend.repository.NoteRepository;
import com.example.backend.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.backend.dto.ai.LinkedItemDto;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContextBuilderService {

    private final TaskRepository taskRepository;
    private final NoteRepository noteRepository;
    private final KeywordExtractor keywordExtractor;
    private final EmbeddingService embeddingService;

    public String buildTasksContext(User user) {
        List<Task> tasks = taskRepository.findByUser(user, PageRequest.of(0, 50, Sort.by("dueDate").ascending()))
                .getContent()
                .stream()
                .filter(t -> t.getStatus() != TaskStatus.DONE)
                .collect(Collectors.toList());

        StringBuilder context = new StringBuilder("[DETAILED TASK LIST]\n");
        if (tasks.isEmpty()) {
            context.append("No pending tasks.\n");
        } else {
            for (Task t : tasks) {
                context.append(String.format("ID: %d | Title: %s | Due: %s | Status: %s | Priority: %s\n", 
                    t.getId(), t.getTitle(), t.getDueDate(), t.getStatus(), t.getPriority()));
            }
        }
        return context.toString();
    }

    public String buildNotesContext(User user) {
        List<Note> notes = noteRepository.findByUser(user, PageRequest.of(0, 20, Sort.by("id").descending()))
                .getContent();

        StringBuilder context = new StringBuilder("[DETAILED NOTE LIST]\n");
        if (notes.isEmpty()) {
            context.append("No recent notes.\n");
        } else {
            for (Note n : notes) {
                context.append(String.format("ID: %d | Title: %s | Content: %s\n", 
                    n.getId(), n.getTitle(), truncateContent(n.getContent())));
            }
        }
        return context.toString();
    }

    public String buildFullContext(User user) {
        return "=== CONTEXT START ===\n" +
                buildTasksContext(user) + "\n" +
                buildNotesContext(user) +
                "\n=== CONTEXT END ===\n";
    }

    public String buildSmartContext(User user, String userMessage) {
        List<String> keywords = keywordExtractor.extract(userMessage);
        
        // Tier 1: Recent
        List<Task> recentTasks = taskRepository.findByUser(user, PageRequest.of(0, 5, Sort.by("dueDate").ascending())).getContent();
        List<Note> recentNotes = noteRepository.findByUser(user, PageRequest.of(0, 5, Sort.by("id").descending())).getContent();
        
        // Tier 2: Keyword Matched
        Set<Task> matchedTasks = new HashSet<>(recentTasks);
        Set<Note> matchedNotes = new HashSet<>(recentNotes);
        
        for (String kw : keywords) {
            matchedTasks.addAll(taskRepository.searchByUser(user, kw, PageRequest.of(0, 3)).getContent());
            matchedNotes.addAll(noteRepository.searchByUser(user, kw, PageRequest.of(0, 3)).getContent());
        }
        
        // Tier 3: Semantic Search
        List<Double> queryVector = embeddingService.getEmbedding(userMessage);
        if (!queryVector.isEmpty()) {
            List<Task> allTasks = taskRepository.findByUser(user, PageRequest.of(0, 100)).getContent();
            List<Note> allNotes = noteRepository.findByUser(user, PageRequest.of(0, 100)).getContent();
            
            if (allTasks.size() + allNotes.size() >= 300) {
                log.warn("PERFORMANCE WARNING: Software-based vector similarity is scanning {} records. Migration to pgvector is HIGHLY RECOMMENDED.", 
                    allTasks.size() + allNotes.size());
            }
            
            List<Task> semanticTasks = allTasks.stream()
                    .filter(t -> t.getEmbedding() != null)
                    .sorted((a, b) -> Double.compare(
                            embeddingService.calculateSimilarity(queryVector, b.getEmbedding()),
                            embeddingService.calculateSimilarity(queryVector, a.getEmbedding())
                    ))
                    .limit(5)
                    .collect(Collectors.toList());
            
            List<Note> semanticNotes = allNotes.stream()
                    .filter(n -> n.getEmbedding() != null)
                    .sorted((a, b) -> Double.compare(
                            embeddingService.calculateSimilarity(queryVector, b.getEmbedding()),
                            embeddingService.calculateSimilarity(queryVector, a.getEmbedding())
                    ))
                    .limit(5)
                    .collect(Collectors.toList());
            
            matchedTasks.addAll(semanticTasks);
            matchedNotes.addAll(semanticNotes);
        }
        
        StringBuilder context = new StringBuilder("=== CONTEXT START ===\n");
        
        context.append("[RELEVANT TASKS]\n");
        if (matchedTasks.isEmpty()) {
            context.append("No specific tasks matched your query keywords.\n");
        } else {
            matchedTasks.stream()
                .filter(t -> t.getStatus() != TaskStatus.DONE)
                .limit(10)
                .forEach(t -> 
                context.append(String.format("ID: %d | Title: %s | Due: %s | Status: %s\n", 
                    t.getId(), t.getTitle(), t.getDueDate(), t.getStatus())));
        }
        
        context.append("\n[RELEVANT NOTES]\n");
        if (matchedNotes.isEmpty()) {
            context.append("No specific notes matched your query keywords.\n");
        } else {
            matchedNotes.stream().limit(10).forEach(n -> 
                context.append(String.format("ID: %d | Title: %s | Content: %s\n", 
                    n.getId(), n.getTitle(), truncateContent(n.getContent()))));
        }
        
        context.append("=== CONTEXT END ===\n");
        return context.toString();
    }

    private String truncateContent(String content) {
        if (content == null) return "";
        return content.length() > 100 ? content.substring(0, 97) + "..." : content;
    }
    public List<LinkedItemDto> getTasksAsItems(User user) {
        return taskRepository.findByUser(user, PageRequest.of(0, 10, Sort.by("dueDate").ascending()))
                .getContent()
                .stream()
                .filter(t -> t.getStatus() != TaskStatus.DONE)
                .map(t -> LinkedItemDto.builder()
                        .id(t.getId())
                        .title(t.getTitle())
                        .type("task")
                        .dueDate(t.getDueDate())
                        .build())
                .collect(Collectors.toList());
    }

    public List<LinkedItemDto> getNotesAsItems(User user) {
        return noteRepository.findByUser(user, PageRequest.of(0, 10, Sort.by("id").descending()))
                .getContent()
                .stream()
                .map(n -> LinkedItemDto.builder()
                        .id(n.getId())
                        .title(n.getTitle())
                        .type("note")
                        .build())
                .collect(Collectors.toList());
    }

    public List<Task> getUpcomingTasks(User user) {
        return taskRepository.findByUser(user, PageRequest.of(0, 3, Sort.by("dueDate").ascending()))
                .getContent()
                .stream()
                .filter(t -> t.getStatus() != TaskStatus.DONE)
                .collect(Collectors.toList());
    }

    public Note getLatestNote(User user) {
        return noteRepository.findByUser(user, PageRequest.of(0, 1, Sort.by("updatedAt").descending()))
                .getContent()
                .stream()
                .findFirst()
                .orElse(null);
    }
}
