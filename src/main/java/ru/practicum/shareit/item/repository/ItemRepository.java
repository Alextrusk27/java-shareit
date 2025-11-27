package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long>, JpaSpecificationExecutor<Item> {

    // выбрал JPQL т.к. меньше кода и расчет на не супер большие объемы запросов (+это по теории курса)
    // если это не best practice, могу переписать с оконными функциями, нативными LATERAL JOIN и т.д.
    @Query("""
            SELECT new ru.practicum.shareit.item.dto.ItemWithBookingsDto(
                i.id,
                i.name,
                i.description,
                i.available,
                new ru.practicum.shareit.booking.dto.BookingInfo(lastB.id, lastB.start, lastB.end, lastB.booker.id),
                new ru.practicum.shareit.booking.dto.BookingInfo(nextB.id, nextB.start, nextB.end, nextB.booker.id)
            )
            FROM Item i
            LEFT JOIN Booking lastB ON lastB.id = (
                SELECT b1.id
                FROM Booking b1
                WHERE b1.item = i
                    AND b1.start <= CURRENT_TIMESTAMP
                    AND b1.status = ru.practicum.shareit.booking.model.BookingStatus.APPROVED
                ORDER BY b1.start DESC
                LIMIT 1
            )
            LEFT JOIN Booking nextB ON nextB.id = (
                SELECT b2.id
                FROM Booking b2
                WHERE b2.item = i
                    AND b2.start > CURRENT_TIMESTAMP
                    AND b2.status = ru.practicum.shareit.booking.model.BookingStatus.APPROVED
                ORDER BY b2.start ASC
                LIMIT 1
            )
            WHERE i.user.id = :userId
            """)
    List<ItemWithBookingsDto> findByUserId(@Param("userId") Long userId);

    boolean existsByIdAndUserId(Long itemId, Long userId);
}
