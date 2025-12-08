package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface ItemRepository extends JpaRepository<Item, Long>, JpaSpecificationExecutor<Item> {

    List<Item> findByOwnerId(Long ownerId);

    boolean existsByIdAndOwnerId(Long itemId, Long ownerId);

    @Query("""
            SELECT i
            FROM Item i
            JOIN i.itemRequests ir
            WHERE ir.id = :requestId
            """)
    List<Item> findItemsByItemRequestId(Long requestId);

    @Query("""
            SELECT ir.id, i
            FROM Item i
            JOIN FETCH i.itemRequests ir
            WHERE ir.id IN :requestIds
            """)
    List<Object[]> findItemsGroupedByRequestIds(List<Long> requestIds);

    default Map<Long, List<Item>> findItemsGroupedByRequestIdsGrouped(List<Long> requestIds) {
        if (requestIds == null || requestIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return findItemsGroupedByRequestIds(requestIds)
                .stream()
                .collect(Collectors.groupingBy(
                        arr -> (Long) arr[0],
                        Collectors.mapping(arr -> (Item) arr[1], Collectors.toList())
                ));
    }
}
