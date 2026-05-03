package com.example.backend.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.exception.UnauthorizedException;
import com.example.backend.model.Note;
import com.example.backend.model.User;
import com.example.backend.repository.NoteRepository;
import com.example.backend.event.NoteEvent;
import org.springframework.context.ApplicationEventPublisher;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final ApplicationEventPublisher eventPublisher;

    public Note createNote(String title, String content, User user) {
        Note note = new Note();
        note.setTitle(title);
        note.setContent(content);
        note.setUser(user);
        Note saved = noteRepository.save(note);
        eventPublisher.publishEvent(new NoteEvent(this, saved.getId(), NoteEvent.EventType.CREATED));
        return saved;
    }
    public Page<Note> searchNotes(String q, User user, Pageable pageable) {
        if (q == null || q.isBlank()) {
            return getNotes(user, pageable);
        }
        return noteRepository.searchByUser(user, q, pageable);
    }


    public Page<Note> getNotes(User user, Pageable pageable) {
        return noteRepository.findByUser(user, pageable);
    }

    public Note getNoteById(Long id, User user) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found"));
        if (!note.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("Unauthorized to access this note");
        }
        return note;
    }
    public Note updateNote(Long noteId, String title, String content, User user) {

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found"));

        if (!note.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("Unauthorized to access the note");
        }

        note.setTitle(title);
        note.setContent(content);
        Note saved = noteRepository.save(note);
        eventPublisher.publishEvent(new NoteEvent(this, saved.getId(), NoteEvent.EventType.UPDATED));
        return saved;
    }

    public void deleteNote(Long noteId, User user) {

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found"));

        if (!note.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("Unauthorized to access the note");
        }

        noteRepository.delete(note);
    }

}
