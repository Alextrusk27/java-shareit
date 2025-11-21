package ru.practicum.shareit.sharing;

import lombok.Getter;

@Getter
public enum EntityType {
    USER("User"),
    ITEM("Item");

    private final String name;

    EntityType(String name) {
        this.name = name;
    }
}
