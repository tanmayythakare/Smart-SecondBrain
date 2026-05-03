package com.example.backend.controller;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import com.example.backend.dto.DTOConverter;
import com.example.backend.dto.TaskDTO;
import com.example.backend.dto.TaskRequest;
import com.example.backend.model.Task;
import com.example.backend.model.TaskStatus;
import com.example.backend.model.User;
import com.example.backend.service.TaskService;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }
    

    @PostMapping
    public TaskDTO createTask(@Valid @RequestBody TaskRequest request, @AuthenticationPrincipal User user) {
        Task task = taskService.createTask(request.getTitle(), request.getPriority(), request.getDueDate(), user);
        return DTOConverter.toDTO(task);
    }

    @GetMapping
    public List<TaskDTO> getTasks(@AuthenticationPrincipal User user, @PageableDefault(size = 100) Pageable pageable) {
        return taskService.getTasks(user, pageable)
                .getContent()
                .stream()
                .map(DTOConverter::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public TaskDTO getTask(@PathVariable Long id, @AuthenticationPrincipal User user) {
        Task task = taskService.getTaskById(id, user);
        return DTOConverter.toDTO(task);
    }

    @PutMapping("/{id}")
    public TaskDTO updateTask(@PathVariable Long id,
                           @Valid @RequestBody TaskRequest request,
                           @AuthenticationPrincipal User user) {
        Task task = taskService.updateTask(id, request.getTitle(), request.isCompleted(), request.getStatus(), request.getPriority(), request.getDueDate(), user);
        return DTOConverter.toDTO(task);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id, @AuthenticationPrincipal User user) {
        taskService.deleteTask(id, user);
        return ResponseEntity.noContent().build();
    }

}
