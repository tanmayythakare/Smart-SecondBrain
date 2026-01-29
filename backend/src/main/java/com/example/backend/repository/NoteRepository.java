package com.example.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.model.Note;
import com.example.backend.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findByUser(User user);
    @Query("""
    		  SELECT n FROM Note n
    		  WHERE n.user = :user
    		    AND (LOWER(n.title) LIKE LOWER(CONCAT('%', :q, '%'))
    		      OR LOWER(n.content) LIKE LOWER(CONCAT('%', :q, '%')))
    		""")
    		List<Note> searchByUser(@Param("user") User user, @Param("q") String q);

}
