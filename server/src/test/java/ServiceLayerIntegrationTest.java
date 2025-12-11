import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.OwnershipException;
import ru.practicum.shareit.exception.UnavailableException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendedDto;
import ru.practicum.shareit.item.dto.request.CreateComment;
import ru.practicum.shareit.item.dto.request.CreateItem;
import ru.practicum.shareit.item.dto.request.UpdateItem;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestExtendedDto;
import ru.practicum.shareit.request.dto.creation.CreateItemRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
@Transactional
@SpringBootTest(
        classes = ShareItApp.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DisplayName("Service Layer Integration Tests")
class ServiceLayerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.1")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name",
                () -> "org.postgresql.Driver");
        registry.add("spring.sql.init.mode", () -> "always");
        registry.add("spring.sql.init.schema-locations", () -> "file:src/main/resources/schema.sql");
    }

    private final EntityManager entityManager;
    private final UserServiceImpl userService;
    private final ItemServiceImpl itemService;
    private final BookingServiceImpl bookingService;
    private final ItemRequestServiceImpl itemRequestService;

    @Nested
    @DisplayName("User Service operations")
    class UserServiceTests {
        @Test
        @DisplayName("Should create new user")
        void createNewUser() {
            CreateUserRequest newUser = new CreateUserRequest("New user", "email@email.com");

            userService.create(newUser);

            List<User> allUsers = getAllUsers();

            assertThat(allUsers)
                    .hasSize(1)
                    .first()
                    .satisfies(u -> {
                        assertThat(u.getId()).isNotNull();
                        assertThat(u.getName()).isEqualTo(newUser.name());
                        assertThat(u.getEmail()).isEqualTo(newUser.email());
                    });
        }

        @Test
        @DisplayName("Should update existing user with valid fields")
        void updateExistingUser() {
            User newUser = addAndGetNewUser();
            UpdateUserRequest updateRequest = new UpdateUserRequest("Updated user", "updatedemail@email.com");

            userService.update(updateRequest, newUser.getId());

            List<User> allUsers = getAllUsers();

            assertThat(allUsers)
                    .hasSize(1)
                    .first()
                    .satisfies(u -> {
                        assertThat(u.getId()).isEqualTo(newUser.getId());
                        assertThat(u.getName()).isEqualTo(updateRequest.name());
                        assertThat(u.getEmail()).isEqualTo(updateRequest.email());
                    });
        }

        @Test
        @DisplayName("Should find user by id")
        void findCreatedUserById() {
            User newUser = addAndGetNewUser();

            UserDto user = userService.findById(newUser.getId());

            assertThat(user).isNotNull();
            assertThat(user.name()).isEqualTo(newUser.getName());
            assertThat(user.email()).isEqualTo(newUser.getEmail());
        }
    }

    @Nested
    @DisplayName("Item Service operations")
    class ItemServiceTests {
        @Test
        @DisplayName("Should create new item")
        void createNewItem() {
            User newUser = addAndGetNewUser();
            CreateItem createRequest = new CreateItem("Item name", "Item description", true, null);

            itemService.create(createRequest, newUser.getId());

            List<Item> items = getAllItems();

            assertThat(items)
                    .hasSize(1)
                    .first()
                    .satisfies(i -> {
                        assertThat(i.getId()).isNotNull();
                        assertThat(i.getName()).isEqualTo(createRequest.name());
                        assertThat(i.getDescription()).isEqualTo(createRequest.description());
                        assertThat(i.getAvailable()).isEqualTo(createRequest.available());
                        assertThat(i.getOwner()).isEqualTo(newUser);
                    });
        }

        @Test
        @DisplayName("Should update existing item with valid fields")
        void updateExistingItem() {
            User newUser = addAndGetNewUser();
            Item newItem = addAndGetNewItem(newUser.getId());
            UpdateItem updateRequest = new UpdateItem("New name", "New description", false);

            itemService.update(updateRequest, newItem.getId(), newUser.getId());

            TypedQuery<Item> query = entityManager.createQuery(
                    "SELECT i FROM Item i WHERE i.name = :name", Item.class);
            Item updatedItem = query.setParameter("name", updateRequest.name()).getSingleResult();

            assertThat(updatedItem.getName()).isEqualTo(updateRequest.name());
            assertThat(updatedItem.getDescription()).isEqualTo(updateRequest.description());
            assertThat(updatedItem.getAvailable()).isEqualTo(updateRequest.available());
        }

        @Test
        @DisplayName("Should find item by id")
        void findItemById() {
            User newUser = addAndGetNewUser();
            Item newItem = addAndGetNewItem(newUser.getId());

            ItemExtendedDto item = itemService.findById(newItem.getId(), newUser.getId());

            assertThat(item).isNotNull();
            assertThat(item.name()).isEqualTo(newItem.getName());
            assertThat(item.description()).isEqualTo(newItem.getDescription());
        }

        @Test
        @DisplayName("Should find all items by owner id")
        void findItemByOwner() {
            User newUser = addAndGetNewUser();
            Item firstItem = addAndGetNewItem(newUser.getId());
            Item secondItem = addAndGetNewItem(newUser.getId());
            Item thirdItem = addAndGetNewItem(newUser.getId());

            List<ItemExtendedDto> foundItems = itemService.findByOwnerId(newUser.getId());
            List<Item> allItems = getAllItems();

            assertThat(foundItems)
                    .hasSize(3)
                    .extracting(ItemExtendedDto::id)
                    .containsExactlyInAnyOrder(firstItem.getId(), secondItem.getId(), thirdItem.getId());
        }

        @Test
        @DisplayName("Should find by query")
        void findItemByQuery() {
            User newUser = addAndGetNewUser();
            Item firstItem = addAndGetNewItem(newUser.getId());
            Item secondItem = addAndGetNewItem(newUser.getId());
            addAndGetNewItem(newUser.getId());

            String trigger = firstItem.getName();

            UpdateItem updateRequest = new UpdateItem(null, trigger, true);
            itemService.update(updateRequest, secondItem.getId(), newUser.getId());

            List<ItemDto> searchResult = itemService.findByQuery(trigger);

            assertThat(searchResult)
                    .hasSize(2)
                    .extracting(ItemDto::id)
                    .containsExactlyInAnyOrder(firstItem.getId(), secondItem.getId());
        }

        @Test
        @DisplayName("Should create comment")
        void createComment() throws InterruptedException {
            User itemOwner = addAndGetNewUser();
            Item item = addAndGetNewItem(itemOwner.getId());
            User booker = addAndGetNewUser();
            LocalDateTime time = LocalDateTime.now();
            Booking booking = addAndGetNewBooking(item.getId(), time, time.plusSeconds(2), booker.getId());
            bookingService.updateBookingStatus(booking.getId(), true, itemOwner.getId());
            CreateComment createRequest = new CreateComment("Some short comment");
            Thread.sleep(3000);

            itemService.createComment(createRequest, item.getId(), booker.getId());

            TypedQuery<Comment> query = entityManager.createQuery(
                    "SELECT c FROM Comment c WHERE c.text = :text", Comment.class);
            Comment foundedComment = query.setParameter("text", createRequest.text()).getSingleResult();

            assertThat(foundedComment).isNotNull();
            assertThat(foundedComment.getId()).isNotNull();
            assertThat(foundedComment.getText()).isEqualTo(createRequest.text());
            assertThat(foundedComment.getItem().getId()).isEqualTo(item.getId());
            assertThat(foundedComment.getAuthor().getId()).isEqualTo(booker.getId());
            assertThat(foundedComment.getCreated()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Booking Service operations")
    class BookingServiceTests {
        @Test
        @DisplayName("Should create new booking")
        void createNewBooking() {
            User owner = addAndGetNewUser();
            Item item = addAndGetNewItem(owner.getId());
            User booker = addAndGetNewUser();
            LocalDateTime startBooking = LocalDateTime.now().plusHours(1);
            LocalDateTime endBooking = startBooking.plusDays(7);
            CreateBookingRequest createRequest = new CreateBookingRequest(item.getId(), startBooking, endBooking);

            bookingService.createBooking(createRequest, booker.getId());

            TypedQuery<Booking> query = entityManager.createQuery(
                    "SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.booker.id = :bookerId", Booking.class);
            query.setParameter("itemId", item.getId());
            query.setParameter("bookerId", booker.getId());

            Booking booking = query.getSingleResult();

            assertThat(booking).isNotNull();
            assertThat(booking.getId()).isNotNull();
            assertThat(booking.getItem().getId()).isEqualTo(item.getId());
            assertThat(booking.getBooker().getId()).isEqualTo(booker.getId());
            assertThat(booking.getStart()).isEqualTo(startBooking);
            assertThat(booking.getEnd()).isEqualTo(endBooking);
            assertThat(booking.getStatus()).isEqualTo(BookingStatus.WAITING);
        }

        @Test
        @DisplayName("Should throw UnavailableException when booking unavailable item")
        void createBookingWithUnavailableItem() {
            User owner = addAndGetNewUser();
            User booker = addAndGetNewUser();

            Item unavailableItem = addAndGetNewItem(owner.getId());
            unavailableItem.setAvailable(false);
            entityManager.merge(unavailableItem);
            entityManager.flush();

            LocalDateTime startBooking = LocalDateTime.now().plusHours(1);
            LocalDateTime endBooking = startBooking.plusDays(7);
            CreateBookingRequest createRequest = new CreateBookingRequest(
                    unavailableItem.getId(),
                    startBooking,
                    endBooking
            );

            UnavailableException exception = assertThrows(UnavailableException.class,
                    () -> bookingService.createBooking(createRequest, booker.getId()));

            assertThat(exception.getMessage())
                    .contains("Item id=" + unavailableItem.getId() + " is unavailable");

            TypedQuery<Booking> query = entityManager.createQuery(
                    "SELECT b FROM Booking b WHERE b.item.id = :itemId", Booking.class);
            query.setParameter("itemId", unavailableItem.getId());

            List<Booking> bookings = query.getResultList();
            assertThat(bookings).isEmpty();
        }

        @Test
        @DisplayName("Should throw UnavailableException when owner tries to book own item")
        void createBooking_ownItem_shouldThrowException() {
            User owner = addAndGetNewUser();
            Item item = addAndGetNewItem(owner.getId());

            LocalDateTime startBooking = LocalDateTime.now().plusHours(1);
            LocalDateTime endBooking = startBooking.plusDays(7);
            CreateBookingRequest createRequest = new CreateBookingRequest(
                    item.getId(),
                    startBooking,
                    endBooking
            );

            UnavailableException exception = assertThrows(UnavailableException.class,
                    () -> bookingService.createBooking(createRequest, owner.getId()));

            assertThat(exception.getMessage())
                    .contains("You cannot book your own item");

            TypedQuery<Booking> query = entityManager.createQuery(
                    "SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.booker.id = :ownerId",
                    Booking.class);
            query.setParameter("itemId", item.getId());
            query.setParameter("ownerId", owner.getId());

            List<Booking> bookings = query.getResultList();
            assertThat(bookings).isEmpty();
        }

        @Test
        @DisplayName("Should find booking by id")
        void findBookingById() {
            User owner = addAndGetNewUser();
            Item item = addAndGetNewItem(owner.getId());
            User booker = addAndGetNewUser();
            LocalDateTime startBooking = LocalDateTime.now().plusHours(5);
            LocalDateTime endBooking = startBooking.plusDays(10);
            Booking newBooking = addAndGetNewBooking(item.getId(), startBooking, endBooking, booker.getId());

            BookingDto foundBooking = bookingService.getBookingById(newBooking.getId(), booker.getId());

            assertThat(foundBooking).isNotNull();
            assertThat(foundBooking.id()).isEqualTo(newBooking.getId());
            assertThat(foundBooking.id()).isEqualTo(newBooking.getId());
            assertThat(foundBooking.start()).isEqualTo(startBooking);
            assertThat(foundBooking.end()).isEqualTo(endBooking);
            assertThat(foundBooking.status()).isEqualTo(BookingStatus.WAITING);
        }

        @Test
        @DisplayName("Should update booking status by item owner")
        void updateBookingStatus() {
            User owner = addAndGetNewUser();
            Item item = addAndGetNewItem(owner.getId());
            User booker = addAndGetNewUser();
            LocalDateTime startBooking = LocalDateTime.now().plusHours(5);
            LocalDateTime endBooking = startBooking.plusDays(10);
            Booking newBooking = addAndGetNewBooking(item.getId(), startBooking, endBooking, booker.getId());

            bookingService.updateBookingStatus(newBooking.getId(), true, owner.getId());

            TypedQuery<Booking> query = entityManager.createQuery(
                    "SELECT b FROM Booking b WHERE b.id = :bookingId", Booking.class);
            query.setParameter("bookingId", newBooking.getId());
            Booking foundBooking = query.getSingleResult();

            assertThat(foundBooking.getStatus()).isEqualTo(BookingStatus.APPROVED);
        }

        @Test
        @DisplayName("Should handle all booking states correctly")
        void getBookingsByBookerIdWithAllStates() {
            User booker = addAndGetNewUser();
            User owner = addAndGetNewUser();

            Item item1 = addAndGetNewItem(owner.getId());
            Item item2 = addAndGetNewItem(owner.getId());
            Item item3 = addAndGetNewItem(owner.getId());

            Booking past = addAndGetNewBooking(item1.getId(),
                    LocalDateTime.now().minusDays(3),
                    LocalDateTime.now().minusDays(1),
                    booker.getId());

            addAndGetNewBooking(item2.getId(),
                    LocalDateTime.now().minusHours(1),
                    LocalDateTime.now().plusHours(1),
                    booker.getId());

            addAndGetNewBooking(item3.getId(),
                    LocalDateTime.now().plusDays(1),
                    LocalDateTime.now().plusDays(3),
                    booker.getId());

            bookingService.updateBookingStatus(past.getId(), false, owner.getId());

            assertThat(bookingService.getBookingsByBookerId(booker.getId(), BookingState.ALL)).hasSize(3);
            assertThat(bookingService.getBookingsByBookerId(booker.getId(), BookingState.PAST)).hasSize(1);
            assertThat(bookingService.getBookingsByBookerId(booker.getId(), BookingState.CURRENT)).hasSize(1);
            assertThat(bookingService.getBookingsByBookerId(booker.getId(), BookingState.FUTURE)).hasSize(1);
            assertThat(bookingService.getBookingsByBookerId(booker.getId(), BookingState.WAITING)).hasSize(2);
            assertThat(bookingService.getBookingsByBookerId(booker.getId(), BookingState.REJECTED)).hasSize(1);
        }

        @Test
        @DisplayName("Should handle all booking states correctly for owner")
        void getBookingsByOwnerIdWithAllStates() {
            User owner = addAndGetNewUser();
            User booker = addAndGetNewUser();

            Item itemForPast = addAndGetNewItem(owner.getId());
            Item itemForCurrent = addAndGetNewItem(owner.getId());
            Item itemForFuture = addAndGetNewItem(owner.getId());

            Booking past = addAndGetNewBooking(itemForPast.getId(),
                    LocalDateTime.now().minusDays(3),
                    LocalDateTime.now().minusDays(1),
                    booker.getId());

            addAndGetNewBooking(itemForCurrent.getId(),
                    LocalDateTime.now().minusHours(1),
                    LocalDateTime.now().plusHours(1),
                    booker.getId());

            addAndGetNewBooking(itemForFuture.getId(),
                    LocalDateTime.now().plusDays(1),
                    LocalDateTime.now().plusDays(3),
                    booker.getId());

            bookingService.updateBookingStatus(past.getId(), false, owner.getId());

            assertThat(bookingService.getBookingsByOwnerId(owner.getId(), BookingState.ALL)).hasSize(3);
            assertThat(bookingService.getBookingsByOwnerId(owner.getId(), BookingState.PAST)).hasSize(1);
            assertThat(bookingService.getBookingsByOwnerId(owner.getId(), BookingState.CURRENT)).hasSize(1);
            assertThat(bookingService.getBookingsByOwnerId(owner.getId(), BookingState.FUTURE)).hasSize(1);
            assertThat(bookingService.getBookingsByOwnerId(owner.getId(), BookingState.WAITING)).hasSize(2);
            assertThat(bookingService.getBookingsByOwnerId(owner.getId(), BookingState.REJECTED)).hasSize(1);
        }

        @Test
        @DisplayName("Should delete booking")
        void deleteBookingById() {
            User owner = addAndGetNewUser();
            User booker = addAndGetNewUser();
            Item item = addAndGetNewItem(owner.getId());

            Booking booking = addAndGetNewBooking(item.getId(),
                    LocalDateTime.now().plusDays(1),
                    LocalDateTime.now().plusDays(3),
                    booker.getId());
            Long bookingId = booking.getId();

            assertThat(entityManager.find(Booking.class, bookingId)).isNotNull();
            bookingService.deleteBookingById(bookingId, booker.getId());
            assertThat(entityManager.find(Booking.class, bookingId)).isNull();
        }

        @Test
        @DisplayName("Should throw OwnershipException when called by non-booker")
        void deleteBookingByIdByNonBooker() {
            User owner = addAndGetNewUser();
            User booker = addAndGetNewUser();
            User otherUser = addAndGetNewUser();
            Item item = addAndGetNewItem(owner.getId());

            Booking booking = addAndGetNewBooking(item.getId(),
                    LocalDateTime.now().plusDays(1),
                    LocalDateTime.now().plusDays(3),
                    booker.getId());

            OwnershipException exception = assertThrows(OwnershipException.class,
                    () -> bookingService.deleteBookingById(booking.getId(), otherUser.getId()));

            assertThat(exception.getMessage())
                    .contains("Only booking author or item owner have access to this resource");

            assertThat(entityManager.find(Booking.class, booking.getId())).isNotNull();
        }
    }

    @Nested
    @DisplayName("Item request Service operations")
    class ItemServiceRequestTests {
        @Test
        @DisplayName("Should create new item request")
        void createNewItemRequest() {
            User author = addAndGetNewUser();
            String text = "New item requested";
            CreateItemRequest createItemRequest = new CreateItemRequest(text);

            itemRequestService.create(author.getId(), createItemRequest);

            TypedQuery<ItemRequest> query = entityManager.createQuery(
                    "SELECT ir FROM ItemRequest ir WHERE ir.description = :description", ItemRequest.class);
            ItemRequest foundedIR = query.setParameter("description", text).getSingleResult();

            assertThat(foundedIR).isNotNull();
            assertThat(foundedIR.getId()).isNotNull();
            assertThat(foundedIR.getAuthor().getId()).isEqualTo(author.getId());
            assertThat(foundedIR.getDescription()).isEqualTo(text);
            assertThat(foundedIR.getCreated()).isNotNull();
        }

        @Test
        @DisplayName("Should find all by author")
        void findAllByAuthor() {
            User author = addAndGetNewUser();
            ItemRequest firstRequest = addAndGetNewItemRequest(author.getId());
            ItemRequest secondRequest = addAndGetNewItemRequest(author.getId());
            User secondAuthor = addAndGetNewUser();
            addAndGetNewItemRequest(secondAuthor.getId());

            List<ItemRequestExtendedDto> itemRequests = itemRequestService.getAllByAuthor(author.getId());

            assertThat(itemRequests)
                    .hasSize(2)
                    .extracting(ItemRequestExtendedDto::id)
                    .containsExactlyInAnyOrder(firstRequest.getId(), secondRequest.getId());
        }

        @Test
        @DisplayName("Should find all")
        void findAllItemRequest() {
            User author = addAndGetNewUser();
            ItemRequest firstRequest = addAndGetNewItemRequest(author.getId());
            ItemRequest secondRequest = addAndGetNewItemRequest(author.getId());
            User secondAuthor = addAndGetNewUser();
            ItemRequest thirdRequest = addAndGetNewItemRequest(secondAuthor.getId());

            List<ItemRequestDto> itemRequests = itemRequestService.getAll();

            assertThat(itemRequests)
                    .hasSize(3)
                    .extracting(ItemRequestDto::id)
                    .containsExactlyInAnyOrder(firstRequest.getId(), secondRequest.getId(), thirdRequest.getId());
        }

        @Test
        @DisplayName("Should find by id")
        void findById() {
            User author = addAndGetNewUser();
            ItemRequest itemRequest = addAndGetNewItemRequest(author.getId());

            ItemRequestExtendedDto foundedIR = itemRequestService.getById(itemRequest.getId());

            assertThat(foundedIR).isNotNull();
            assertThat(foundedIR.id()).isEqualTo(itemRequest.getId());
            assertThat(foundedIR.description()).isEqualTo(itemRequest.getDescription());
        }
    }

    private User addAndGetNewUser() {
        CreateUserRequest createRequest = new CreateUserRequest(
                generateRandomString(15),
                generateRandomString(10) + "@" + generateRandomString(5) + ".com");
        userService.create(createRequest);
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u WHERE u.name = :name", User.class);
        return query.setParameter("name", createRequest.name()).getSingleResult();
    }

    private Item addAndGetNewItem(long userId) {
        CreateItem createRequest = new CreateItem(
                generateRandomString(15),
                generateRandomString(50),
                true,
                null);
        itemService.create(createRequest, userId);
        TypedQuery<Item> query = entityManager.createQuery(
                "SELECT i FROM Item i WHERE i.name = :name", Item.class);
        return query.setParameter("name", createRequest.name()).getSingleResult();
    }

    private Booking addAndGetNewBooking(long itemId, LocalDateTime start, LocalDateTime end, long userId) {
        CreateBookingRequest createRequest = new CreateBookingRequest(itemId, start, end);
        bookingService.createBooking(createRequest, userId);
        TypedQuery<Booking> query = entityManager.createQuery(
                "SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.booker.id = :userId", Booking.class);
        query.setParameter("itemId", itemId);
        query.setParameter("userId", userId);
        return query.getSingleResult();
    }

    private ItemRequest addAndGetNewItemRequest(long userId) {
        CreateItemRequest createRequest = new CreateItemRequest(generateRandomString(40));
        itemRequestService.create(userId, createRequest);
        TypedQuery<ItemRequest> query = entityManager.createQuery(
                "SELECT ir FROM ItemRequest ir WHERE ir.description = :description", ItemRequest.class);
        return query.setParameter("description", createRequest.description()).getSingleResult();
    }

    private List<User> getAllUsers() {
        TypedQuery<User> query = entityManager.createQuery("SELECT u FROM User u", User.class);
        return query.getResultList();
    }

    private List<Item> getAllItems() {
        TypedQuery<Item> query = entityManager.createQuery("SELECT i FROM Item i", Item.class);
        return query.getResultList();
    }

    private String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }
}
