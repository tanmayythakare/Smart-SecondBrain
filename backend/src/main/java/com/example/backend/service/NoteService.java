package com.example.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.backend.model.Note;
import com.example.backend.model.User;
import com.example.backend.repository.NoteRepository;

@Service
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public Note createNote(String title, String content, User user) {
        Note note = new Note();
        note.setTitle(title);
        note.setContent(content);
        note.setUser(user);
        return noteRepository.save(note);
    }
    public List<Note> searchNotes(String q, User user) {
    	  return noteRepository.searchByUser(user, q);
    	}


    public List<Note> getNotes(User user) {
        return noteRepository.findByUser(user);
    }
    public Note updateNote(Long noteId, String title, String content, User user) {

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        if (!note.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to note");
        }

        note.setTitle(title);
        note.setContent(content);
        return noteRepository.save(note);
    }

    public void deleteNote(Long noteId, User user) {

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        if (!note.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to note");
        }

        noteRepository.delete(note);
    }

}
