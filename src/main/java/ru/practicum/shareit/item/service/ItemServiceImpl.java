package ru.practicum.shareit.item.service;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingInfo;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.OwnershipException;
import ru.practicum.shareit.exceptions.UnavailableException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendedDto;
import ru.practicum.shareit.item.dto.request.CreateCommentRequest;
import ru.practicum.shareit.item.dto.request.CreateItemRequest;
import ru.practicum.shareit.item.dto.request.UpdateItemRequest;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.sharing.EntityFinder;
import ru.practicum.shareit.sharing.EntityType;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final BookingMapper bookingMapper;
    private final EntityFinder entityFinder;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ItemDto create(CreateItemRequest createRequest, Long userId) {
        User owner = entityFinder.findOrThrow(userRepository, userId, EntityType.USER);

        Item item = itemMapper.toItemFromCreate(createRequest);
        item.setOwner(owner);
        Item savedItem = itemRepository.save(item);

        return itemMapper.toItemDto(savedItem);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ItemDto update(UpdateItemRequest updateRequest, Long id, Long userId) {
        entityFinder.findOrThrow(userRepository, userId, EntityType.USER);
        Item existingItem = entityFinder.findOrThrow(itemRepository, id, EntityType.ITEM);
        checkItemOwnership(id, userId);

        itemMapper.updateItem(updateRequest, existingItem);
        Item updatedItem = itemRepository.save(existingItem);
        return itemMapper.toItemDto(updatedItem);
    }

    @Transactional(readOnly = true)
    @Override
    public ItemExtendedDto findById(Long id, Long userId) {
        entityFinder.findOrThrow(userRepository, userId, EntityType.USER);
        Item item = entityFinder.findOrThrow(itemRepository, id, EntityType.ITEM);

        List<CommentDto> comments = commentRepository.findByItemId(id)
                .stream()
                .map(commentMapper::toCommentDto)
                .toList();

        BookingInfo lastBooking = null;
        BookingInfo nextBooking = null;

        if (!userId.equals(item.getOwner().getId())) {
            return itemMapper.toExtendedDto(
                    item,
                    lastBooking,
                    nextBooking,
                    comments
            );
        }
        lastBooking = bookingRepository.findLastBookingByItemId(id)
                .map(bookingMapper::toBookingInfo)
                .orElse(null);
        nextBooking = bookingRepository.findNextBookingByItemId(id)
                .map(bookingMapper::toBookingInfo)
                .orElse(null);

        return itemMapper.toExtendedDto(
                item,
                lastBooking,
                nextBooking,
                comments
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemExtendedDto> findByOwnerId(Long userId) {
        entityFinder.findOrThrow(userRepository, userId, EntityType.USER);
        List<Item> items = itemRepository.findByOwnerId(userId);

        if (items.isEmpty()) {
            return List.of();
        }

        List<Long> itemsIds = items.stream()
                .map(Item::getId)
                .toList();

        Map<Long, BookingInfo> lastBookings = bookingRepository.findLastBookingsByItemIds(itemsIds)
                .stream()
                .collect(Collectors.toMap(booking -> booking.getItem().getId(), bookingMapper::toBookingInfo));

        Map<Long, BookingInfo> nextBookings = bookingRepository.findNextBookingsByItemIds(itemsIds)
                .stream()
                .collect(Collectors.toMap(booking -> booking.getItem().getId(), bookingMapper::toBookingInfo));

        Map<Long, List<CommentDto>> comments = commentRepository.findByItemIds(itemsIds)
                .stream()
                .collect(Collectors.groupingBy(
                        comment -> comment.getItem().getId(),
                        Collectors.mapping(
                                commentMapper::toCommentDto,
                                Collectors.toList()
                        )
                ));

        return items.stream()
                .map(item -> itemMapper.toExtendedDto(
                        item,
                        lastBookings.get(item.getId()),
                        nextBookings.get(item.getId()),
                        comments.get(item.getId()))
                )
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
    public void delete(Long itemId, Long userId) {
        checkItemOwnership(itemId, userId);
        itemRepository.deleteById(itemId);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public CommentDto createComment(CreateCommentRequest createRequest, Long itemId, Long authorId) {
        User booker = entityFinder.findOrThrow(userRepository, authorId, EntityType.USER);
        Item item = entityFinder.findOrThrow(itemRepository, itemId, EntityType.ITEM);
        List<Booking> bookings = bookingRepository.findByItemAndBooker(itemId, authorId);

        if (bookings.isEmpty()) {
            throw new OwnershipException("User id=%d hasn't approved bookings for this item".formatted(authorId));
        }

        if (bookings.stream()
                .noneMatch(booking -> booking.getEnd().isBefore(LocalDateTime.now()))) {
            throw new UnavailableException("Booking doesn't finish yet");
        }

        Comment comment = commentMapper.toCommentFromCreate(createRequest);
        comment.setItem(item);
        comment.setAuthor(booker);
        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toCommentDto(savedComment);
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

    public void checkItemOwnership(Long itemId, Long userId) {
        if (!itemRepository.existsByIdAndOwnerId(itemId, userId)) {
            throw new OwnershipException("User id=%d is not owner of item id=%d"
                    .formatted(userId, itemId));
        }
    }
}
