package com.example.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.backend.model.Task;
import com.example.backend.model.User;
import com.example.backend.repository.TaskRepository;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task createTask(String title, User user) {
        Task task = new Task();
        task.setTitle(title);
        task.setUser(user);
        return taskRepository.save(task);
    }

    public List<Task> getTasks(User user) {
        return taskRepository.findByUser(user);
    }
    public Task updateTask(Long taskId, String title, User user) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to task");
        }

        task.setTitle(title);
        return taskRepository.save(task);
    }

    public void deleteTask(Long taskId, User user) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to task");
        }

        taskRepository.delete(task);
    }

}
