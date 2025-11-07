package ru.practicum.shareit.items;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.users.InMemoryUserRepositoryTest;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

@TestConfiguration
public class ItemsTestConfig {
    static final long[] ALL_ITEM_IDS = {1L, 2L, 3L};
    static final long TEST_ITEM_GENERATOR_DEFAULT = Arrays.stream(ALL_ITEM_IDS).max().orElse(0L);
    static final String TEST_ITEM_1_NAME = "Washing machine";
    static final String TEST_ITEM_2_NAME = "Robot vacuum cleaner";
    static final String TEST_ITEM_3_NAME = "Screwdriver";
    static final String TEST_ITEM_1_DESCRIPTION = "Easy laundry";
    static final String TEST_ITEM_2_DESCRIPTION = "An indispensable assistant";
    static final String TEST_ITEM_3_DESCRIPTION = "Best machine ever";
    static final long TEST_ITEMS_1_AND_2_OWNER_ID = 1L;
    static final long TEST_ITEM_3_OWNER_ID = 2L;
    static final long TEST_NO_ITEMS_OWNER_ID = 3L;
    static final String TEST_NEW_ITEM_NAME = "Blender";
    static final String TEST_NEW_ITEM_DESCRIPTION = "It's helps you create your best";
    static final long TEST_NON_EXISTENT_ID = 9999L;

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Item firstTestItem() {
        return Item.builder()
                .id(ALL_ITEM_IDS[0])
                .name(TEST_ITEM_1_NAME)
                .description(TEST_ITEM_1_DESCRIPTION)
                .ownerId(TEST_ITEMS_1_AND_2_OWNER_ID)
                .available(true)
                .build();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Item secondTestItem() {
        return Item.builder()
                .id(ALL_ITEM_IDS[1])
                .name(TEST_ITEM_2_NAME)
                .description(TEST_ITEM_2_DESCRIPTION)
                .ownerId(TEST_ITEMS_1_AND_2_OWNER_ID)
                .available(true)
                .build();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Item thirdTestItem() {
        return Item.builder()
                .id(ALL_ITEM_IDS[2])
                .name(TEST_ITEM_3_NAME)
                .description(TEST_ITEM_3_DESCRIPTION)
                .ownerId(TEST_ITEM_3_OWNER_ID)
                .available(true)
                .build();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public CreateItemRequest createRequest() {
        return new CreateItemRequest(
                TEST_NEW_ITEM_NAME,
                TEST_NEW_ITEM_DESCRIPTION,
                true);
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public UpdateItemRequest updateRequest() {
        return new UpdateItemRequest(
                TEST_NEW_ITEM_NAME,
                TEST_NEW_ITEM_DESCRIPTION,
                true);
    }

    @Bean
    public AtomicLong userIdGenerator() {
        return new AtomicLong(0);
    }

    @Bean
    InMemoryUserRepositoryTest userRepository(Map<Long, User> users, Set<String> emails,
                                              AtomicLong userIdGenerator) {
        return new InMemoryUserRepositoryTest(users, emails, userIdGenerator);
    }

    @Bean
    public AtomicLong itemIdGenerator() {
        return new AtomicLong(0);
    }

    @Bean
    public InMemoryItemRepositoryTest itemRepository(Map<Long, Item> items, AtomicLong itemIdGenerator) {
        return new InMemoryItemRepositoryTest(items, itemIdGenerator);
    }

    @Bean
    @Primary
    public ItemServiceImplTest itemService(InMemoryItemRepositoryTest itemRepository,
                                           InMemoryUserRepositoryTest userRepository) {
        return new ItemServiceImplTest(itemRepository, userRepository);
    }

}
