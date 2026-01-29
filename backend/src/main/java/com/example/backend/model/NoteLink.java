package com.example.backend.model;

import jakarta.persistence.*;

@Entity
public class NoteLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_note_id", nullable = false)
    private Note sourceNote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_note_id", nullable = false)
    private Note targetNote;

    // getters & setters
    public Long getId() {
        return id;
    }

    public Note getSourceNote() {
        return sourceNote;
    }

    public void setSourceNote(Note sourceNote) {
        this.sourceNote = sourceNote;
    }

    public Note getTargetNote() {
        return targetNote;
    }

    public void setTargetNote(Note targetNote) {
        this.targetNote = targetNote;
    }
}
