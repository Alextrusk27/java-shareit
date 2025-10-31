package ru.practicum.shareit.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    public AtomicLong idGenerator() {
        return new AtomicLong(1);
    }
}