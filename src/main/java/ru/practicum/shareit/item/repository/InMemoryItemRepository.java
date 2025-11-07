package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.ItemOwnershipException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@RequiredArgsConstructor
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> items;
    private final AtomicLong itemIdGenerator;

    @Override
    public Item save(Item item) {
        long id = itemIdGenerator.incrementAndGet();
        item.setId(id);
        items.put(id, item);
        return item;
    }

    @Override
    public Item update(Item item, long itemId) {
        Item existingItem = items.get(itemId);

        Optional.ofNullable(item.getName())
                .filter(name -> !name.isBlank())
                .ifPresent(existingItem::setName);

        Optional.ofNullable(item.getDescription())
                .filter(description -> !description.isBlank())
                .ifPresent(existingItem::setDescription);

        Optional.ofNullable(item.getAvailable())
                .ifPresent(existingItem::setAvailable);

        return existingItem;
    }

    @Override
    public Optional<Item> findById(long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public Optional<List<Item>> findByOwnerId(long ownerId) {
        return Optional.of(items.values().stream()
                .filter(item -> item.getOwnerId() == ownerId)
                .toList());
    }

    @Override
    public List<Item> findByQuery(String query) {
        query = query.toLowerCase();
        String[] queryArray = query.split("\\s+");

        return items.values().stream()
                .filter(item -> item.getAvailable() == true)
                .filter(item -> {
                    String nameLower = item.getName().toLowerCase();
                    String descLower = item.getDescription().toLowerCase();

                    return Arrays.stream(queryArray)
                            .filter(word -> word.length() > 2)
                            .anyMatch(word ->
                                    nameLower.contains(word) || descLower.contains(word)
                            );
                })
                .toList();
    }

    @Override
    public void delete(long itemId) {
        items.remove(itemId);
    }

    public void validateItemExists(long itemId) {
        if (!items.containsKey(itemId)) {
            throw new NotFoundException("Item id=%d not found".formatted(itemId));
        }
    }

    public void validateItemOwner(long itemId, long ownerId) {
        validateItemExists(itemId);
        Item item = items.get(itemId);
        if (item.getOwnerId() != ownerId) {
            throw new ItemOwnershipException("User id=%d is not owned by item id=%d"
                    .formatted(ownerId, itemId));
        }
    }

    protected void clear() {
        items.clear();
        itemIdGenerator.set(0);
    }
}
