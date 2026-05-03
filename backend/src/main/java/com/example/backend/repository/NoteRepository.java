package com.example.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.model.Note;
import com.example.backend.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NoteRepository extends JpaRepository<Note, Long> {

    Page<Note> findByUser(User user, Pageable pageable);
    @Query("""
    		  SELECT n FROM Note n
    		  WHERE n.user = :user
    		    AND (LOWER(n.title) LIKE LOWER(CONCAT('%', :q, '%'))
    		      OR LOWER(n.content) LIKE LOWER(CONCAT('%', :q, '%')))
    		""")
    Page<Note> searchByUser(@Param("user") User user, @Param("q") String q, Pageable pageable);
    boolean existsByIdAndUser(Long id, User user);

}
