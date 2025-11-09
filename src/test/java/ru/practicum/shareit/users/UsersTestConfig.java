package ru.practicum.shareit.users;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.items.InMemoryItemRepositoryTest;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

@TestConfiguration
public class UsersTestConfig {
    static final long TEST_USER_1_ID = 1L;
    static final long TEST_USER_2_ID = 2L;
    static final String TEST_USER_1_NAME = "first_name";
    static final String TEST_USER_1_EMAIL = "first@email.com";
    static final String TEST_USER_2_NAME = "second_name";
    static final String TEST_USER_2_EMAIL = "second@email.com";

    @Bean
    public AtomicLong userIdGenerator() {
        return new AtomicLong(0);
    }

    @Bean
    public AtomicLong itemIdGenerator() {
        return new AtomicLong(0);
    }

    @Bean
    public InMemoryUserRepositoryTest userRepository(Map<Long, User> testUsers, Set<String> testEmails,
                                                     AtomicLong userIdGenerator) {
        return new InMemoryUserRepositoryTest(testUsers, testEmails, userIdGenerator);
    }

    @Bean
    public InMemoryItemRepositoryTest itemRepository(Map<Long, Item> testItems,
                                                     AtomicLong itemIdGenerator) {
        return new InMemoryItemRepositoryTest(testItems, itemIdGenerator);
    }

    @Bean
    @Primary
    public UserServiceImplTest userService(UserMapper userMapper,
                                           InMemoryUserRepositoryTest userRepository,
                                           InMemoryItemRepositoryTest itemRepository) {
        return new UserServiceImplTest(userMapper, userRepository, itemRepository);
    }
}