package com.example.backend.repository;

import com.example.backend.model.AiFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AiFeedbackRepository extends JpaRepository<AiFeedback, Long> {
}
