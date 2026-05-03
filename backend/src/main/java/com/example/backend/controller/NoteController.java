package com.example.backend.controller;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.example.backend.repository.UserRepository;

import com.example.backend.dto.DTOConverter;
import com.example.backend.dto.NoteDTO;
import com.example.backend.model.Note;
import com.example.backend.model.User;
import com.example.backend.service.NoteService;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import java.util.stream.Collectors;

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
    public Page<NoteDTO> searchNotes(@RequestParam String q, @AuthenticationPrincipal User user, @PageableDefault(size = 10) Pageable pageable) {
        return noteService.searchNotes(q, user, pageable)
                .map(DTOConverter::toDTO);
    }

    @GetMapping("/{id}")
    public NoteDTO getNote(@PathVariable Long id, @AuthenticationPrincipal User user) {
        Note note = noteService.getNoteById(id, user);
        return DTOConverter.toDTO(note);
    }

    @PostMapping
    public NoteDTO createNote(@Valid @RequestBody NoteDTO request, @AuthenticationPrincipal User user) {
        Note note = noteService.createNote(
                request.getTitle(),
                request.getContent(),
                user
        );
        return DTOConverter.toDTO(note);
    }

    @GetMapping
    public Page<NoteDTO> getNotes(@AuthenticationPrincipal User user, @PageableDefault(size = 10) Pageable pageable) {
        return noteService.getNotes(user, pageable)
                .map(DTOConverter::toDTO);
    }

    @PutMapping("/{id}")
    public NoteDTO updateNote(@PathVariable Long id,
                           @Valid @RequestBody NoteDTO request,
                           @AuthenticationPrincipal User user) {
        Note note = noteService.updateNote(
                id,
                request.getTitle(),
                request.getContent(),
                user
        );
        return DTOConverter.toDTO(note);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id, @AuthenticationPrincipal User user) {
        noteService.deleteNote(id, user);
        return ResponseEntity.noContent().build();
    }

}
