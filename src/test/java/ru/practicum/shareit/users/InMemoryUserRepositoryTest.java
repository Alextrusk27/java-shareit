//package ru.practicum.shareit.users;
//
//import lombok.Getter;
//import ru.practicum.shareit.user.model.User;
//import ru.practicum.shareit.user.repository.InMemoryUserRepository;
//
//import java.util.Map;
//import java.util.Set;
//import java.util.concurrent.atomic.AtomicLong;
//
//@Getter
//public class InMemoryUserRepositoryTest extends InMemoryUserRepository {
//    private final Map<Long, User> users;
//    private final Set<String> emails;
//    private final AtomicLong userIdGenerator;
//
//    public InMemoryUserRepositoryTest(Map<Long, User> users, Set<String> emails, AtomicLong userIdGenerator) {
//        super(users, emails, userIdGenerator);
//        this.users = users;
//        this.emails = emails;
//        this.userIdGenerator = userIdGenerator;
//    }
//
//    @Override
//    public void clear() {
//        super.clear();
//    }
//}
