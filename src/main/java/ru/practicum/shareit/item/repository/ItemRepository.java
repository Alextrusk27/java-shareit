package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.dto.projection.ItemWithBookingProjection;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long>, JpaSpecificationExecutor<Item> {

    @Query(value = """
            SELECT
                i.id,
                i.name,
                i.description,
                i.available,
                last_booking.id as lastBookingId,
                last_booking.start_date as lastBookingStart,
                last_booking.end_date as lastBookingEnd,
                last_booking.booker_id as lastBookerId,
                next_booking.id as nextBookingId,
                next_booking.start_date as nextBookingStart,
                next_booking.end_date as nextBookingEnd,
                next_booking.booker_id as nextBookerId
            FROM items i
            LEFT JOIN LATERAL (
                SELECT
                b.id, b.start_date, b.end_date, b.booker_id
                FROM bookings b
                WHERE b.item_id = i.id
                    AND b.status = 'APPROVED'
                    AND ((b.start_date <= CURRENT_TIMESTAMP AND b.end_date >= CURRENT_TIMESTAMP)
                        OR b.end_date < CURRENT_TIMESTAMP)
                ORDER BY b.end_date DESC
                LIMIT 1
            ) last_booking ON true
            LEFT JOIN LATERAL (
                SELECT
                b.id, b.start_date, b.end_date, b.booker_id
                FROM bookings b
                WHERE b.item_id = i.id
                    AND b.status = 'APPROVED'
                    AND b.start_date > CURRENT_TIMESTAMP
                ORDER BY b.start_date ASC
                LIMIT 1
            ) next_booking ON true
            WHERE i.id = :itemId
            """, nativeQuery = true)
    ItemWithBookingProjection findItemWithBookingInfo(@Param("itemId") Long itemId);

    @Query(value = """
            SELECT
                i.id,
                i.name,
                i.description,
                i.available,
                last_booking.id as lastBookingId,
                last_booking.start_date as lastBookingStart,
                last_booking.end_date as lastBookingEnd,
                last_booking.booker_id as lastBookerId,
                next_booking.id as nextBookingId,
                next_booking.start_date as nextBookingStart,
                next_booking.end_date as nextBookingEnd,
                next_booking.booker_id as nextBookerId
            FROM items i
            LEFT JOIN LATERAL (
                SELECT
                b.id, b.start_date, b.end_date, b.booker_id
                FROM bookings b
                WHERE b.item_id = i.id
                    AND b.status = 'APPROVED'
                    AND ((b.start_date <= CURRENT_TIMESTAMP AND b.end_date >= CURRENT_TIMESTAMP)
                        OR b.end_date < CURRENT_TIMESTAMP)
                ORDER BY b.end_date DESC
                LIMIT 1
            ) last_booking ON true
            LEFT JOIN LATERAL (
                SELECT
                b.id, b.start_date, b.end_date, b.booker_id
                FROM bookings b
                WHERE b.item_id = i.id
                    AND b.status = 'APPROVED'
                    AND b.start_date > CURRENT_TIMESTAMP
                ORDER BY b.start_date ASC
                LIMIT 1
            ) next_booking ON true
            WHERE i.owner_id = :userId
            ORDER BY i.id
            """, nativeQuery = true)
    List<ItemWithBookingProjection> findItemsWithBookingInfo(@Param("userId") Long userId);

    boolean existsByIdAndOwnerId(Long itemId, Long ownerId);
}
