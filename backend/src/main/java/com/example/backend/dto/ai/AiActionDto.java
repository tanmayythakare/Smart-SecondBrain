package com.example.backend.dto.ai;

import lombok.Data;
import java.util.Map;

@Data
public class AiActionDto {
    private ActionType type;
    private Map<String, Object> data;

    public enum ActionType {
        CREATE_TASK,
        UPDATE_TASK,
        DELETE_TASK,
        CREATE_NOTE,
        UPDATE_NOTE,
        DELETE_NOTE,
        UNKNOWN
    }
}
