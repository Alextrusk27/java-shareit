package ru.practicum.shareit.exceptions;

import java.util.List;

public record ErrorResponse(String message, List<String> error) {
}
