package ru.practicum.shareit.item.service;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.ItemOwnershipException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.sharing.EntityFinder;
import ru.practicum.shareit.sharing.EntityType;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;
    private final EntityFinder entityFinder;

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ItemDto create(CreateItemRequest createRequest, long userId) {
        User owner = entityFinder.findOrThrow(userRepository, userId, EntityType.USER);

        Item item = itemMapper.toItemFromCreate(createRequest);
        item.setUser(owner);
        Item savedItem = itemRepository.save(item);

        return itemMapper.toItemDto(savedItem);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ItemDto update(UpdateItemRequest updateRequest, long id, long userId) {
        entityFinder.findOrThrow(userRepository, userId, EntityType.USER);
        Item existingItem = entityFinder.findOrThrow(itemRepository, id, EntityType.ITEM);
        checkItemOwnership(id, userId);

        itemMapper.updateItem(updateRequest, existingItem);
        Item updatedItem = itemRepository.save(existingItem);
        return itemMapper.toItemDto(updatedItem);
    }

    @Override
    public ItemDto findById(long id) {
        Item item = entityFinder.findOrThrow(itemRepository, id, EntityType.ITEM);
        return itemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> findByOwnerId(long userId) {
        entityFinder.findOrThrow(userRepository, userId, EntityType.USER);
        return itemRepository.findByUserId(userId)
                .stream()
                .map(itemMapper::toItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> findByQuery(String text) {
        if (text == null || text.trim().isEmpty()) {
            return List.of();
        }
        Specification<Item> spec = createSearchSpecification(text);
        return itemRepository.findAll(spec)
                .stream()
                .map(itemMapper::toItemDto)
                .toList();
    }

    @Override
    @Transactional
    public void delete(long itemId, long userId) {
        checkItemOwnership(itemId, userId);
        itemRepository.deleteById(itemId);
    }

    private Specification<Item> createSearchSpecification(String text) {
        return (root, query, cb) -> {
            Predicate availablePredicate = cb.isTrue(root.get("available"));

            Predicate[] predicates = Arrays.stream(text.toLowerCase().split("\\s+"))
                    .filter(word -> word.length() >= 2)
                    .map(word -> {
                        String pattern = "%" + word + "%";
                        return cb.or(
                                cb.like(cb.lower(root.get("name")), pattern),
                                cb.like(cb.lower(root.get("description")), pattern)
                        );
                    })
                    .toArray(Predicate[]::new);

            if (predicates.length > 0) {
                return cb.and(availablePredicate, cb.or(predicates));
            } else {
                return cb.disjunction();
            }
        };
    }

    private void checkItemOwnership(long itemId, long userId) {
        if (!itemRepository.existsByIdAndUserId(itemId, userId)) {
            throw new ItemOwnershipException("User id=%d is not owner of item id=%d"
                    .formatted(userId, itemId));
        }
    }
}
