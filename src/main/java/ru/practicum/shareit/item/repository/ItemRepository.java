package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    Item save(Item item);

    Item update(Item item, long itemId);

    Optional<Item> findById(long itemId);

    List<Item> findByOwnerId(long ownerId);

    List<Item> findByQuery(String query);

    void delete(long itemId);
}
