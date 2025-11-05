package ru.practicum.shareit.item.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Item {
    Long id;
    String name;
    String description;
    Long ownerId;
    Boolean available;
}
