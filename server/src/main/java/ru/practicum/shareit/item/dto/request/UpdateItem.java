package ru.practicum.shareit.item.dto.request;

public record UpdateItem(String name, String description, Boolean available) {

    public boolean hasName() {
        return name != null && !name.isBlank();
    }

    public boolean hasDescription() {
        return description != null && !description.isBlank();
    }

    public boolean hasAvailable() {
        return available != null;
    }
}