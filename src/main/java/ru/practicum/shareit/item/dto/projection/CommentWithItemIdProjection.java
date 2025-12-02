package ru.practicum.shareit.item.dto.projection;

import java.time.LocalDateTime;

public interface CommentWithItemIdProjection {
    Long getId();

    String getAuthorName();

    String getText();

    LocalDateTime getCreated();

    Long getItemId();
}
