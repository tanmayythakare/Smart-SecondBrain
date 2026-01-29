package com.example.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.model.Note;
import com.example.backend.model.NoteLink;

public interface NoteLinkRepository extends JpaRepository<NoteLink, Long> {
	
    boolean existsBySourceNoteAndTargetNote(Note sourceNote, Note targetNote);
    List<NoteLink> findBySourceNote(Note sourceNote);

    List<NoteLink> findByTargetNote(Note targetNote);
}
