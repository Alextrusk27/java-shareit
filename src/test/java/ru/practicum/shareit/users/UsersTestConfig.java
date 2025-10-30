package ru.practicum.shareit.users;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.InMemoryUserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.*;

@TestConfiguration
public class UsersTestConfig {
    static final long TEST_USER_1_ID = 1L;
    static final long TEST_USER_2_ID = 2L;
    static final String TEST_USER_1_NAME = "first_name";
    static final String TEST_USER_1_EMAIL = "first@email.com";
    static final String TEST_USER_2_NAME = "second_name";
    static final String TEST_USER_2_EMAIL = "second@email.com";

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public User firstTestUser() {
        return User.builder()
                    .id(TEST_USER_1_ID)
                    .name(TEST_USER_1_NAME)
                    .email(TEST_USER_1_EMAIL)
                    .build();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public User secondTestUserNoId() {
        return User.builder()
                .name(TEST_USER_2_NAME)
                .email(TEST_USER_2_EMAIL)
                .build();
    }

    @Bean(name = "testUsers")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public TreeMap<Long, User> users(User firstTestUser) {
        return new TreeMap<>() {{
            put(1L, firstTestUser);
        }};
    }

    @Bean(name = "testEmails")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public HashSet<String> emails(User firstTestUser) {
        return new HashSet<>() {{
            add(firstTestUser.getEmail());
        }};
    }

    @Bean
    @Primary
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public InMemoryUserRepository userRepository(TreeMap<Long, User> testUsers, HashSet<String> testEmails) {
        return new InMemoryUserRepository(testUsers, testEmails);
    }

    @Bean
    @Primary
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public UserServiceImpl userService(InMemoryUserRepository userRepository) {
        return new UserServiceImpl(userRepository);
    }
}