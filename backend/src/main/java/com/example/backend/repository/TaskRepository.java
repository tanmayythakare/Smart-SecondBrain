package com.example.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.model.Task;
import com.example.backend.model.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findByUser(User user, Pageable pageable);
    
    @org.springframework.data.jpa.repository.Query("""
        SELECT t FROM Task t
        WHERE t.user = :user
        AND LOWER(t.title) LIKE LOWER(CONCAT('%', :q, '%'))
    """)
    Page<Task> searchByUser(@org.springframework.data.repository.query.Param("user") User user, @org.springframework.data.repository.query.Param("q") String q, Pageable pageable);
    
    boolean existsByIdAndUser(Long id, User user);
}
