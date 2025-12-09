package ru.practicum.shareit.item.dto.request;

public record CreateItem(String name, String description, Boolean available, Long requestId) {
}
