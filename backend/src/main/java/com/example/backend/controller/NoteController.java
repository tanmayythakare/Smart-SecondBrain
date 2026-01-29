package com.example.backend.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.example.backend.repository.UserRepository;

import com.example.backend.model.Note;
import com.example.backend.model.User;
import com.example.backend.service.NoteService;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;
    private final UserRepository userRepository;

    public NoteController(NoteService noteService, UserRepository userRepository) {
        this.noteService = noteService;
        this.userRepository = userRepository;
    }

    @GetMapping("/search")
    public List<Note> searchNotes(@RequestParam String q) {
      User user = getCurrentUser();
      return noteService.searchNotes(q, user);
    }


    @PostMapping
    public Note createNote(@RequestBody Note request) {
        User user = getCurrentUser();
        return noteService.createNote(
                request.getTitle(),
                request.getContent(),
                user
        );
    }

    @GetMapping
    public List<Note> getNotes() {
        User user = getCurrentUser();
        return noteService.getNotes(user);
    }
    @PutMapping("/{id}")
    public Note updateNote(@PathVariable Long id,
                           @RequestBody Note request) {
        User user = getCurrentUser();
        return noteService.updateNote(
                id,
                request.getTitle(),
                request.getContent(),
                user
        );
    }

    @DeleteMapping("/{id}")
    public void deleteNote(@PathVariable Long id) {
        User user = getCurrentUser();
        noteService.deleteNote(id, user);
    }


    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        return userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

}
