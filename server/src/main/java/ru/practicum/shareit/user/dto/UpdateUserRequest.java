package ru.practicum.shareit.user.dto;

public record UpdateUserRequest(String name, String email) {
    public boolean hasName() {
        return name != null && !name.isBlank();
    }

    public boolean hasEmail() {
        return email != null && !email.isBlank();
    }
}
