package ru.practicum.shareit.exception;

import java.util.List;

public record ErrorResponse(String message, List<String> error) {
}
