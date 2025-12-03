package ru.practicum.shareit.sharing;

import lombok.Getter;

@Getter
public enum EntityType {
    USER("User"),
    ITEM("Item"),
    BOOKING("Booking"),;

    private final String name;

    EntityType(String name) {
        this.name = name;
    }
}
