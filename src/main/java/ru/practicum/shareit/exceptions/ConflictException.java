package ru.practicum.shareit.exceptions;

public class ConflictException extends RuntimeException {
    private final String parameter;
    private final String reason;

    public ConflictException(String parameter, String reason) {
        this.parameter = parameter;
        this.reason = reason;
    }
}
