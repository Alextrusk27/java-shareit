package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("""
            SELECT b
            FROM Booking b
            WHERE b.item.owner.id = :userId
            ORDER BY b.start DESC
            """)
    List<Booking> findByItemOwner(@Param("userId") Long userId);

    @Query("""
            SELECT b
            FROM Booking b
            WHERE b.item.owner.id = :userId
            AND CURRENT_TIMESTAMP BETWEEN b.start AND b.end
            ORDER BY b.start DESC""")
    List<Booking> findCurrentByItemOwner(@Param("userId") Long userId);

    @Query("""
            SELECT b
            FROM Booking b
            WHERE b.item.owner.id = :userId
            AND b.end < CURRENT_TIMESTAMP
            ORDER BY b.start DESC""")
    List<Booking> findPastByItemOwner(@Param("userId") Long userId);

    @Query("""
            SELECT b
            FROM Booking b
            WHERE b.item.owner.id = :userId
            AND b.start > CURRENT_TIMESTAMP
            ORDER BY b.start DESC""")
    List<Booking> findFutureByItemOwner(@Param("userId") Long userId);

    @Query("""
            SELECT b
            FROM Booking b
            WHERE b.item.owner.id = :userId
            AND b.status = :status
            ORDER BY b.start DESC""")
    List<Booking> findByItemOwnerAndStatus(@Param("userId") Long userId,
                                           @Param("status") BookingStatus status);

    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long userId, BookingStatus status);

    @Query("""
            SELECT b
            FROM Booking b
            WHERE b.booker.id = :userId
            AND CURRENT_TIMESTAMP BETWEEN b.start AND b.end
            ORDER BY b.start DESC""")
    List<Booking> findCurrentByBooker(@Param("userId") Long userId);

    @Query("""
            SELECT b
            FROM Booking b
            WHERE b.booker.id = :userId
            AND b.end < CURRENT_TIMESTAMP
            ORDER BY b.start DESC""")
    List<Booking> findPastByBooker(@Param("userId") Long userId);

    @Query("""
            SELECT b
            FROM Booking b
            WHERE b.booker.id = :userId
            AND b.start > CURRENT_TIMESTAMP
            ORDER BY b.start DESC""")
    List<Booking> findFutureByBooker(@Param("userId") Long userId);

    @Query("""
            SELECT b
            FROM Booking b
            WHERE b.item.id = :itemId
                AND b.booker.id = :bookerId
                AND b.status = 'APPROVED'""")
    List<Booking> findByItemAndBooker(@Param("itemId") Long itemId,
                                      @Param("bookerId") Long bookerId);
}
