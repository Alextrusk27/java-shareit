package ru.practicum.shareit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

@Configuration
public class AppConfig {

    @Bean
    public Map<Long, User> users() {
        return new HashMap<>();
    }

    @Bean
    public Set<String> emails() {
        return new HashSet<>();
    }

    @Bean
    public Map<Long, Item> items() {
        return new HashMap<>();
    }

    @Bean
    public AtomicLong userIdGenerator() {
        return new AtomicLong(0);
    }

    @Bean
    public AtomicLong itemIdGenerator() {
        return new AtomicLong(0);
    }
}