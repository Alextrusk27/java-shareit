package ru.practicum.shareit.exceptions;

public class UnavailableException extends  RuntimeException {
    public UnavailableException(String message) {
        super(message);
    }
}
