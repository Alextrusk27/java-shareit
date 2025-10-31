package ru.practicum.shareit.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.shareit.user.model.User;

import java.util.HashSet;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;

@Configuration
public class AppConfig {

    @Bean
    public TreeMap<Long, User> users() {
        return new TreeMap<>();
    }

    @Bean
    public HashSet<String> emails() {
        return new HashSet<>();
    }

    @Bean
    public AtomicLong idGenerator() {
        return new AtomicLong(1);
    }
}