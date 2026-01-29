package com.example.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.backend.model.Note;
import com.example.backend.model.NoteLink;
import com.example.backend.model.User;
import com.example.backend.repository.NoteLinkRepository;
import com.example.backend.repository.NoteRepository;

@Service
public class NoteLinkService {

    private final NoteLinkRepository noteLinkRepository;
    private final NoteRepository noteRepository;
    
    public NoteLinkService(NoteLinkRepository noteLinkRepository,
                           NoteRepository noteRepository) {
        this.noteLinkRepository = noteLinkRepository;
        this.noteRepository = noteRepository;
    }

    public void linkNotes(Long sourceId, Long targetId, User user) {

        Note source = noteRepository.findById(sourceId)
                .orElseThrow(() -> new RuntimeException("Source note not found"));

        Note target = noteRepository.findById(targetId)
                .orElseThrow(() -> new RuntimeException("Target note not found"));

        // ownership check (CRITICAL)
        if (!source.getUser().getId().equals(user.getId())
            || !target.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized note linking");
        }

        // prevent duplicate links
        if (noteLinkRepository.existsBySourceNoteAndTargetNote(source, target)) {
            return; // already linked, silently ignore
        }

        NoteLink link = new NoteLink();
        link.setSourceNote(source);
        link.setTargetNote(target);

        noteLinkRepository.save(link);
    }public List<Note> getBacklinks(Long noteId, User user) {

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        if (!note.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access");
        }

        return noteLinkRepository.findByTargetNote(note)
                .stream()
                .map(NoteLink::getSourceNote)
                .toList();
    }


    public List<Note> getRelatedNotes(Long noteId, User user) {

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        if (!note.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access");
        }

        List<NoteLink> outgoing = noteLinkRepository.findBySourceNote(note);

        return
            outgoing.stream()
                .map(NoteLink::getTargetNote)
                .collect(Collectors.toSet()) // remove duplicates
                .stream()
                .collect(Collectors.toList());
    }
  }

