package com.example.backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.backend.dto.TaskRequest;
import com.example.backend.model.Task;
import com.example.backend.model.User;
import com.example.backend.service.TaskService;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }
    

    @PostMapping
    public Task createTask(@RequestBody TaskRequest request) {
        User user = getCurrentUser();
        return taskService.createTask(request.getTitle(), user);
    }

    @GetMapping
    public ResponseEntity<?> getTasks() {
        try {
            User user = getCurrentUser();
            return ResponseEntity.ok(taskService.getTasks(user));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Failed to fetch tasks"));
        }
    }

    @PutMapping("/{id}")
    public Task updateTask(@PathVariable Long id,
                           @RequestBody TaskRequest request) {
        User user = getCurrentUser();
        return taskService.updateTask(id, request.getTitle(), user);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        User user = getCurrentUser();
        taskService.deleteTask(id, user);
    }


    private User getCurrentUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }
}
