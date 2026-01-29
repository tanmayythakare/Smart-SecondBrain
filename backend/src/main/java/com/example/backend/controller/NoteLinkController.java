package com.example.backend.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.backend.model.Note;
import com.example.backend.model.User;
import com.example.backend.service.NoteLinkService;

@RestController
@RequestMapping("/api/note-links")
public class NoteLinkController {

    private final NoteLinkService noteLinkService;

    public NoteLinkController(NoteLinkService noteLinkService) {
        this.noteLinkService = noteLinkService;
    }
    
    @GetMapping("/backlinks/{noteId}")
    public List<Note> getBacklinks(@PathVariable Long noteId) {
        User user = getCurrentUser();
        return noteLinkService.getBacklinks(noteId, user);
    }


    @PostMapping
    public void linkNotes(@RequestParam Long sourceId,
                          @RequestParam Long targetId) {
        User user = getCurrentUser();
        noteLinkService.linkNotes(sourceId, targetId, user);
    }

    @GetMapping("/{noteId}")
    public List<Note> getRelatedNotes(@PathVariable Long noteId) {
        User user = getCurrentUser();
        return noteLinkService.getRelatedNotes(noteId, user);
    }

    private User getCurrentUser() {
        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();
        return (User) auth.getPrincipal();
    }
}
