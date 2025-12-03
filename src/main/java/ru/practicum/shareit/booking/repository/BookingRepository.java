package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("""
            SELECT b
            FROM Booking b
            WHERE b.item.owner.id = :userId
            ORDER BY b.start DESC
            """)
    List<Booking> findByItemOwner(Long userId);

    @Query("""
            SELECT b
            FROM Booking b
            WHERE b.item.owner.id = :userId
            AND CURRENT_TIMESTAMP BETWEEN b.start AND b.end
            ORDER BY b.start DESC""")
    List<Booking> findCurrentByItemOwner(Long userId);

    @Query("""
            SELECT b
            FROM Booking b
            WHERE b.item.owner.id = :userId
            AND b.end < CURRENT_TIMESTAMP
            ORDER BY b.start DESC""")
    List<Booking> findPastByItemOwner(Long userId);

    @Query("""
            SELECT b
            FROM Booking b
            WHERE b.item.owner.id = :userId
            AND b.start > CURRENT_TIMESTAMP
            ORDER BY b.start DESC""")
    List<Booking> findFutureByItemOwner(Long userId);

    @Query("""
            SELECT b
            FROM Booking b
            WHERE b.item.owner.id = :userId
            AND b.status = :status
            ORDER BY b.start DESC""")
    List<Booking> findByItemOwnerAndStatus(Long userId, BookingStatus status);

    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long userId, BookingStatus status);

    @Query("""
            SELECT b
            FROM Booking b
            WHERE b.booker.id = :userId
            AND CURRENT_TIMESTAMP BETWEEN b.start AND b.end
            ORDER BY b.start DESC""")
    List<Booking> findCurrentByBooker(Long userId);

    @Query("""
            SELECT b
            FROM Booking b
            WHERE b.booker.id = :userId
            AND b.end < CURRENT_TIMESTAMP
            ORDER BY b.start DESC""")
    List<Booking> findPastByBooker(Long userId);

    @Query("""
            SELECT b
            FROM Booking b
            WHERE b.booker.id = :userId
            AND b.start > CURRENT_TIMESTAMP
            ORDER BY b.start DESC""")
    List<Booking> findFutureByBooker(Long userId);

    @Query("""
            SELECT b
            FROM Booking b
            WHERE b.item.id = :itemId
                AND b.booker.id = :bookerId
                AND b.status = 'APPROVED'""")
    List<Booking> findByItemAndBooker(Long itemId, Long bookerId);

    @Query("""
            SELECT b
            FROM Booking b
            WHERE (b.item.id, b.start) IN (
                SELECT b2.item.id, MAX(b2.start)
                FROM Booking b2
                WHERE b2.item.id IN :itemIds
                    AND b2.status = 'APPROVED'
                    AND b2.start <= CURRENT_TIMESTAMP
                GROUP BY b2.item.id)
            """)
    List<Booking> findLastBookingsByItemIds(List<Long> itemIds);

    @Query("""
            SELECT b
            FROM Booking b
            WHERE (b.item.id, b.start) IN (
                SELECT b2.item.id, MAX(b2.start)
                FROM Booking b2
                WHERE b2.item.id = :itemId
                    AND b2.status = 'APPROVED'
                    AND b2.start <= CURRENT_TIMESTAMP
                GROUP BY b2.item.id)
            """)
    Optional<Booking> findLastBookingByItemId(Long itemId);

    @Query("""
            SELECT b
            FROM Booking b
            WHERE (b.item.id, b.start) IN (
                SELECT b2.item.id, MIN(b2.start)
                FROM Booking b2
                WHERE b2.item.id IN :itemIds
                    AND b2.status = 'APPROVED'
                    AND b2.start >= CURRENT_TIMESTAMP
                GROUP BY b2.item.id)
            """)
    List<Booking> findNextBookingsByItemIds(List<Long> itemIds);

    @Query("""
            SELECT b
            FROM Booking b
            WHERE (b.item.id, b.start) IN (
                SELECT b2.item.id, MIN(b2.start)
                FROM Booking b2
                WHERE b2.item.id = :itemId
                    AND b2.status = 'APPROVED'
                    AND b2.start >= CURRENT_TIMESTAMP
                GROUP BY b2.item.id)
            """)
    Optional<Booking> findNextBookingByItemId(Long itemId);
}
