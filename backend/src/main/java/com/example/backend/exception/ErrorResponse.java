package com.example.backend.exception;

import java.time.LocalDateTime;

public class ErrorResponse {

    private String message;
    private String code;
    private LocalDateTime timestamp;

    public ErrorResponse(String message, String code) {
        this.message = message;
        this.code = code;
        this.timestamp = LocalDateTime.now();
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
