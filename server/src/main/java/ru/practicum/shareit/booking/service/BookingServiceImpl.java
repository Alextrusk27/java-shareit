package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.OwnershipException;
import ru.practicum.shareit.exception.UnavailableException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.sharing.EntityFinder;
import ru.practicum.shareit.sharing.EntityType;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;
    private final EntityFinder entityFinder;

    @Override
    @Transactional
    public BookingDto createBooking(CreateBookingRequest createRequest, Long userId) {
        User booker = entityFinder.findOrThrow(userRepository, userId, EntityType.USER);
        Item bookedItem = entityFinder.findOrThrow(itemRepository, createRequest.itemId(), EntityType.ITEM);

        if (!bookedItem.getAvailable()) {
            throw new UnavailableException("Item id=%d is unavailable".formatted(bookedItem.getId()));
        }
        if (bookedItem.getOwner().getId().equals(userId)) {
            throw new UnavailableException("You cannot book your own item");
        }

        Booking booking = bookingMapper.toBookingFromCreate(createRequest);

        booking.setBooker(booker);
        booking.setItem(bookedItem);
        booking.setStatus(BookingStatus.WAITING);

        Booking savedBooking = bookingRepository.save(booking);
        return bookingMapper.toBookingDto(savedBooking);
    }

    @Override
    @Transactional
    public BookingDto updateBookingStatus(Long bookingId, boolean approved, Long userId) {
        Booking booking = entityFinder.findOrThrow(bookingRepository, bookingId, EntityType.BOOKING);
        long itemId = booking.getItem().getId();

        if (!itemRepository.existsByIdAndOwnerId(itemId, userId)) {
            throw new OwnershipException("User id=%d is not owner of item id=%d"
                    .formatted(userId, itemId));
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto getBookingById(Long bookingId, Long userId) {
        Booking booking = entityFinder.findOrThrow(bookingRepository, bookingId, EntityType.BOOKING);
        boolean isBooker = userId.equals(booking.getBooker().getId());
        boolean isOwner = userId.equals(booking.getItem().getOwner().getId());

        if (!isBooker && !isOwner) {
            throw new OwnershipException("Only booking author or item owner have access to this resource");
        }

        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getBookingsByBookerId(Long userId, BookingState state) {
        entityFinder.findOrThrow(userRepository, userId, EntityType.USER);

        List<Booking> result = switch (state) {
            case ALL -> bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
            case CURRENT -> bookingRepository.findCurrentByBooker(userId);
            case PAST -> bookingRepository.findPastByBooker(userId);
            case FUTURE -> bookingRepository.findFutureByBooker(userId);
            case WAITING -> bookingRepository
                    .findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case REJECTED -> bookingRepository
                    .findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
        };

        return result
                .stream()
                .map(bookingMapper::toBookingDto)
                .toList();
    }

    @Override
    public List<BookingDto> getBookingsByOwnerId(Long userId, BookingState state) {
        entityFinder.findOrThrow(userRepository, userId, EntityType.USER);

        List<Booking> result = switch (state) {
            case ALL -> bookingRepository.findByItemOwner(userId);
            case CURRENT -> bookingRepository.findCurrentByItemOwner(userId);
            case PAST -> bookingRepository.findPastByItemOwner(userId);
            case FUTURE -> bookingRepository.findFutureByItemOwner(userId);
            case WAITING -> bookingRepository
                    .findByItemOwnerAndStatus(userId, BookingStatus.WAITING);
            case REJECTED -> bookingRepository
                    .findByItemOwnerAndStatus(userId, BookingStatus.REJECTED);
        };

        return result
                .stream()
                .map(bookingMapper::toBookingDto)
                .toList();
    }

    @Override
    @Transactional
    public void deleteBookingById(Long bookingId, Long userId) {
        User bookingAuthor = entityFinder.findOrThrow(userRepository, userId, EntityType.USER);
        Booking booking = entityFinder.findOrThrow(bookingRepository, bookingId, EntityType.BOOKING);

        if (!bookingAuthor.equals(booking.getBooker())) {
            throw new OwnershipException("Only booking author or item owner have access to this resource");
        }

        bookingRepository.delete(booking);
    }
}
