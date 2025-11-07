package ru.practicum.shareit.items;

import lombok.Getter;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.users.InMemoryUserRepositoryTest;

import java.util.Map;

import static ru.practicum.shareit.items.ItemsTestConfig.*;

@Getter
public class ItemServiceImplTest extends ItemServiceImpl {
    private final InMemoryUserRepositoryTest testUserRepository;
    private final InMemoryItemRepositoryTest testItemRepository;

    public ItemServiceImplTest(InMemoryItemRepositoryTest itemRepository, InMemoryUserRepositoryTest userRepository) {
        super(itemRepository, userRepository);
        this.testItemRepository = itemRepository;
        this.testUserRepository = userRepository;
        loadTestUsersData();
    }

    public void resetTestData() {
        testItemRepository.clear();

        testItemRepository.getItemIdGenerator().set(TEST_ITEM_GENERATOR_DEFAULT);

        Map<Long, Item> items = testItemRepository.getItems();
        items.put(ALL_ITEM_IDS[0], Item.builder()
                .id(ALL_ITEM_IDS[0])
                .name(TEST_ITEM_1_NAME)
                .description(TEST_ITEM_1_DESCRIPTION)
                .available(true)
                .ownerId(TEST_ITEMS_1_AND_2_OWNER_ID)
                .build());
        items.put(ALL_ITEM_IDS[1], Item.builder()
                .id(ALL_ITEM_IDS[1])
                .name(TEST_ITEM_2_NAME)
                .description(TEST_ITEM_2_DESCRIPTION)
                .available(true)
                .ownerId(TEST_ITEMS_1_AND_2_OWNER_ID)
                .build());
        items.put(ALL_ITEM_IDS[2], Item.builder()
                .id(ALL_ITEM_IDS[2])
                .name(TEST_ITEM_3_NAME)
                .description(TEST_ITEM_3_DESCRIPTION)
                .available(true)
                .ownerId(TEST_ITEM_3_OWNER_ID)
                .build());
    }

    private void loadTestUsersData() {
        Map<Long, User> users = testUserRepository.getUsers();
        users.put(TEST_ITEMS_1_AND_2_OWNER_ID, User.builder()
                .id(TEST_ITEMS_1_AND_2_OWNER_ID)
                .name("random name")
                .email("random@email.com")
                .build());
        users.put(TEST_ITEM_3_OWNER_ID, User.builder()
                .id(TEST_ITEM_3_OWNER_ID)
                .name("some name")
                .email("some@email.com")
                .build());
        users.put(TEST_NO_ITEMS_OWNER_ID, User.builder()
                .id(TEST_NO_ITEMS_OWNER_ID)
                .name("unknown name")
                .email("unknown@email.com")
                .build());

        testUserRepository.getEmails().add("random@email.com");
        testUserRepository.getEmails().add("some@email.com");
        testUserRepository.getEmails().add("unknown@email.com");

        testUserRepository.getUserIdGenerator().set(3L);
    }
}
