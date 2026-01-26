package com.example.backend.controller;

import java.util.List;

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
    public List<Task> getTasks() {
        User user = getCurrentUser();
        return taskService.getTasks(user);
    }

    private User getCurrentUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }
}
