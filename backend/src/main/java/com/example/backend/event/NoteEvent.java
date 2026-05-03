package com.example.backend.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class NoteEvent extends ApplicationEvent {
    private final Long noteId;
    private final EventType type;

    public enum EventType {
        CREATED, UPDATED
    }

    public NoteEvent(Object source, Long noteId, EventType type) {
        super(source);
        this.noteId = noteId;
        this.type = type;
    }
}
