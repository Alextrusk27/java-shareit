//package ru.practicum.shareit.items;
//
//import lombok.Getter;
//import ru.practicum.shareit.item.model.Item;
//import ru.practicum.shareit.item.repository.InMemoryItemRepository;
//
//import java.util.Map;
//import java.util.concurrent.atomic.AtomicLong;
//
//@Getter
//public class InMemoryItemRepositoryTest extends InMemoryItemRepository {
//    private final Map<Long, Item> items;
//    private final AtomicLong itemIdGenerator;
//
//    public InMemoryItemRepositoryTest(Map<Long, Item> items, AtomicLong itemIdGenerator) {
//        super(items, itemIdGenerator);
//        this.items = items;
//        this.itemIdGenerator = itemIdGenerator;
//    }
//
//    @Override
//    public void clear() {
//        super.clear();
//    }
//}
