package ru.practicum.shareit.users;

import lombok.Getter;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.items.InMemoryItemRepositoryTest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Map;

import static ru.practicum.shareit.users.UsersTestConfig.*;

@Getter
public class UserServiceImplTest extends UserServiceImpl {
    private final InMemoryUserRepositoryTest testUserRepository;
    private final InMemoryItemRepositoryTest testItemRepository;

    public UserServiceImplTest(InMemoryUserRepositoryTest userRepository, InMemoryItemRepositoryTest itemRepository) {
        super(userRepository, itemRepository);
        this.testUserRepository = userRepository;
        this.testItemRepository = itemRepository;
    }

    public void refreshUsersTestData() {
        testUserRepository.clear();

        testUserRepository.getUsers()
                .put(TEST_USER_1_ID, User.builder()
                        .id(TEST_USER_1_ID)
                        .name(TEST_USER_1_NAME)
                        .email(TEST_USER_1_EMAIL)
                        .build());
        testUserRepository.getEmails().add(TEST_USER_1_EMAIL);
        testUserRepository.getUserIdGenerator().set(TEST_USER_1_ID);
    }

    public void refreshItemsTestData() {
        testItemRepository.clear();
        Map<Long, Item> items = testItemRepository.getItems();
        items.put(1L, Item.builder()
                .id(1L)
                .name("Name_x")
                .description("Description_x")
                .available(true)
                .ownerId(TEST_USER_1_ID)
                .build());
        items.put(2L, Item.builder()
                .id(2L)
                .name("Name_y")
                .description("Description_y")
                .available(false)
                .ownerId(TEST_USER_1_ID)
                .build());
        items.put(3L, Item.builder()
                .id(3L)
                .name("Name_z")
                .description("Description_z")
                .available(true)
                .ownerId(TEST_USER_2_ID)
                .build());
    }
}
