package com.example.backend.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.exception.UnauthorizedException;
import com.example.backend.model.Task;
import com.example.backend.model.TaskStatus;
import com.example.backend.model.TaskPriority;
import com.example.backend.model.User;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import com.example.backend.repository.TaskRepository;
import com.example.backend.ai.AiSearchService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final AiSearchService aiSearchService;

    public Page<Task> searchTasks(String q, User user, Pageable pageable) {
        if (q == null || q.isBlank()) {
            return getTasks(user, pageable);
        }
        return taskRepository.searchByUser(user, q, pageable);
    }

    public Task createTask(String title, String priority, String dueDate, User user) {
        Task task = new Task();
        task.setTitle(title);
        task.setPriority(parsePriority(priority));
        task.setDueDate(parseDate(dueDate));
        task.setUser(user);
        Task saved = taskRepository.save(task);
        aiSearchService.generateAndSaveTaskEmbedding(saved.getId());
        return saved;
    }

    public Page<Task> getTasks(User user, Pageable pageable) {
        return taskRepository.findByUser(user, pageable);
    }

    public Task getTaskById(Long id, User user) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        if (!task.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("Unauthorized to access this task");
        }
        return task;
    }
    public Task updateTask(Long taskId, String title, boolean completed, String status, String priority, String dueDate, User user) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (!task.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("Unauthorized to access the task");
        }

        task.setTitle(title);
        task.setStatus(parseStatus(status, completed));
        task.setPriority(parsePriority(priority));
        task.setDueDate(parseDate(dueDate));
        Task saved = taskRepository.save(task);
        aiSearchService.generateAndSaveTaskEmbedding(saved.getId());
        return saved;
    }

    private TaskStatus parseStatus(String s, boolean completed) {
        if (completed) return TaskStatus.DONE;
        if (s == null) return TaskStatus.TODO;
        try { return TaskStatus.valueOf(s.toUpperCase()); }
        catch (Exception e) { return TaskStatus.TODO; }
    }

    private TaskPriority parsePriority(String p) {
        if (p == null) return TaskPriority.MEDIUM;
        try { return TaskPriority.valueOf(p.toUpperCase()); }
        catch (Exception e) { return TaskPriority.MEDIUM; }
    }

    private LocalDate parseDate(String d) {
        if (d == null || d.isBlank()) return null;
        try {
            return LocalDate.parse(d);
        } catch (Exception e) {
            return LocalDate.now().plusDays(3); // fallback as per safe parsing recommendation
        }
    }

    public void deleteTask(Long taskId, User user) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (!task.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("Unauthorized to access the task");
        }

        taskRepository.delete(task);
    }

}
