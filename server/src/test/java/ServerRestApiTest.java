import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendedDto;
import ru.practicum.shareit.item.dto.request.CreateComment;
import ru.practicum.shareit.item.dto.request.CreateItem;
import ru.practicum.shareit.item.dto.request.UpdateItem;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestExtendedDto;
import ru.practicum.shareit.request.dto.creation.CreateItemRequest;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.sharing.HttpHeader;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("REST API Unit tests")
class ServerRestApiTest {

    private MockMvc mvc;

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Nested
    @DisplayName("User endpoints")
    class UserControllerTests {
        @Mock
        private UserServiceImpl userService;

        @InjectMocks
        private UserController userController;

        @BeforeEach
        void setUp() {
            mvc = MockMvcBuilders
                    .standaloneSetup(userController)
                    .setControllerAdvice(new ExceptionController())
                    .build();
        }

        @Test
        @DisplayName("POST /users → should create user and return 201")
        void createUser_validRequest_shouldReturnCreatedUser() throws Exception {
            UserDto userDto = new UserDto(1L, "John Locke", "email@email.com");
            CreateUserRequest request = new CreateUserRequest(userDto.name(), userDto.email());

            when(userService.create(any(CreateUserRequest.class)))
                    .thenReturn(userDto);

            mvc.perform(post("/users")
                            .content(mapper.writeValueAsString(request))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id", is(userDto.id()), Long.class))
                    .andExpect(jsonPath("$.name", is(userDto.name())))
                    .andExpect(jsonPath("$.email", is(userDto.email())));

            verify(userService, times(1)).create(any(CreateUserRequest.class));
        }

        @Test
        @DisplayName("POST /users with duplicate email → should return 409")
        void createUser_withDuplicateEmail_shouldReturnConflict() throws Exception {
            String duplicateEmail = "existing@email.com";
            CreateUserRequest request = new CreateUserRequest("John Locke", duplicateEmail);
            String errorMessage = "Email %s already exists".formatted(duplicateEmail);

            when(userService.create(any(CreateUserRequest.class)))
                    .thenThrow(new DuplicateException(errorMessage));

            mvc.perform(post("/users")
                            .content(mapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isConflict())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(containsString(errorMessage)));

            verify(userService).create(any(CreateUserRequest.class));
        }

        @Test
        @DisplayName("PATCH /users/{userId} → should update user and return 200")
        void updateUser_validRequest_shouldReturnUpdatedUser() throws Exception {
            long userId = 1L;
            UpdateUserRequest request = new UpdateUserRequest("Updated Name", "updated@email.com");
            UserDto updatedUser = new UserDto(userId, "Updated Name", "updated@email.com");

            when(userService.update(any(UpdateUserRequest.class), eq(userId)))
                    .thenReturn(updatedUser);

            mvc.perform(patch("/users/{userId}", userId)
                            .content(mapper.writeValueAsString(request))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(updatedUser.id()), Long.class))
                    .andExpect(jsonPath("$.name", is(updatedUser.name())))
                    .andExpect(jsonPath("$.email", is(updatedUser.email())));

            verify(userService).update(any(UpdateUserRequest.class), eq(userId));
        }

        @Test
        @DisplayName("PATCH /users/{userId} update non existing user → should return 404")
        void updateUser_nonExistingId_shouldReturnNotFound() throws Exception {
            long nonExistingId = 999L;
            UpdateUserRequest request = new UpdateUserRequest("Updated Name", "updated@email.com");
            String errorMessage = "User id=%d not found".formatted(nonExistingId);

            when(userService.update(any(UpdateUserRequest.class), eq(nonExistingId)))
                    .thenThrow(new NotFoundException(errorMessage));

            mvc.perform(patch("/users/{userId}", nonExistingId)
                            .content(mapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(containsString(errorMessage)));

            verify(userService).update(any(UpdateUserRequest.class), eq(nonExistingId));
        }

        @Test
        @DisplayName("GET /users/{userId} → should return 200 with user data")
        void getUserById_existingUser_shouldReturnUser() throws Exception {
            long userId = 1L;
            UserDto userDto = new UserDto(userId, "John Locke", "email@email.com");

            when(userService.findById(eq(userId)))
                    .thenReturn(userDto);

            mvc.perform(get("/users/{userId}", userId)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(userDto.id()), Long.class))
                    .andExpect(jsonPath("$.name", is(userDto.name())))
                    .andExpect(jsonPath("$.email", is(userDto.email())));

            verify(userService).findById(eq(userId));
        }

        @Test
        @DisplayName("DELETE /users/{userId} → should delete user and return 204")
        void deleteUserById_existingUser_shouldReturnNoContent() throws Exception {
            long userId = 1L;

            doNothing().when(userService).delete(userId);

            mvc.perform(delete("/users/{userId}", userId))
                    .andExpect(status().isNoContent());

            verify(userService).delete(userId);
        }
    }

    @Nested
    @DisplayName("Item endpoints")
    class ItemControllerTests {
        @Mock
        private ItemServiceImpl itemService;

        @InjectMocks
        private ItemController itemController;

        @BeforeEach
        void setup() {
            mvc = MockMvcBuilders
                    .standaloneSetup(itemController)
                    .setControllerAdvice(new ExceptionController())
                    .build();
        }

        @Test
        @DisplayName("POST /items → should create item and return 201")
        void  createItem_validRequest_shouldReturnCreatedItem() throws Exception {
            Long userId = 1L;
            ItemDto itemDto = new ItemDto(1L, "name", "desc", true);
            CreateItem request = new CreateItem(itemDto.name(), itemDto.description(), itemDto.available(), null);

            when(itemService.create(any(CreateItem.class), eq(userId)))
                    .thenReturn(itemDto);

            mvc.perform(post("/items")
                            .header(HttpHeader.USER_ID, userId)
                            .content(mapper.writeValueAsString(request))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id", is(itemDto.id()), Long.class))
                    .andExpect(jsonPath("$.name", is(itemDto.name())))
                    .andExpect(jsonPath("$.description", is(itemDto.description())))
                    .andExpect(jsonPath("$.available", is(itemDto.available())));

            verify(itemService).create(any(CreateItem.class), eq(userId));
        }

        @Test
        @DisplayName("PATCH /items/{id} → should update item and return 200")
        void updateItem_validRequest_shouldReturnUpdatedItem() throws Exception {
            Long userId = 1L;
            Long itemId = 2L;
            ItemDto itemDto = new ItemDto(itemId, "name", "desc", true);
            UpdateItem request = new UpdateItem(itemDto.name(), itemDto.description(), itemDto.available());

            when(itemService.update(any(UpdateItem.class), eq(itemId), eq(userId)))
                    .thenReturn(itemDto);

            mvc.perform(patch("/items/{id}", itemId)
                            .header(HttpHeader.USER_ID, userId)
                            .content(mapper.writeValueAsString(request))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(itemDto.id()), Long.class))
                    .andExpect(jsonPath("$.name", is(itemDto.name())))
                    .andExpect(jsonPath("$.description", is(itemDto.description())))
                    .andExpect(jsonPath("$.available", is(itemDto.available())));

            verify(itemService).update(any(UpdateItem.class), eq(itemId), eq(userId));
        }

        @Test
        @DisplayName("PATCH /items/{id} update not own item → should return 403")
        void updateItem_fromNotOwner_shouldReturnForbidden() throws Exception {
            Long notOwner = 5L;
            Long itemId = 2L;
            String errorMessage = "User id=%d is not owner of item id=%d".formatted(notOwner, itemId);
            UpdateItem request = new UpdateItem("name", "desc", true);

            when(itemService.update(any(UpdateItem.class), eq(itemId), eq(notOwner)))
                    .thenThrow(new OwnershipException(errorMessage));

            mvc.perform(patch("/items/{id}", itemId)
                            .header(HttpHeader.USER_ID, notOwner)
                            .content(mapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(containsString(errorMessage)));

            verify(itemService).update(any(UpdateItem.class), eq(itemId), eq(notOwner));
        }

        @Test
        @DisplayName("GET /items/{id} → should return 200 with item data")
        void getItemById_existingItem_shouldReturnItem() throws Exception {
            long userId = 1L;
            long itemId = 4L;
            ItemExtendedDto itemDto = new ItemExtendedDto(itemId, "name", "desc", true, null, null, null);

            when(itemService.findById(eq(itemId), eq(userId)))
                    .thenReturn(itemDto);

            mvc.perform(get("/items/{id}", itemId)
                            .header(HttpHeader.USER_ID, userId)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(itemDto.id()), Long.class))
                    .andExpect(jsonPath("$.name", is(itemDto.name())))
                    .andExpect(jsonPath("$.description", is(itemDto.description())));

            verify(itemService).findById(eq(itemId), eq(userId));
        }

        @Test
        @DisplayName("GET /items → should return 200 with all user items")
        void getAllItems_byExistingUser_shouldReturnItemList() throws Exception {
            long userId = 1L;
            long firstItemId = 4L;
            long secondItemId = 8L;
            ItemExtendedDto firstIemDto = new ItemExtendedDto(firstItemId, "name", "desc", true, null, null, null);
            ItemExtendedDto secondItemDto = new ItemExtendedDto(secondItemId, "another name", "another desc", false,
                    null, null, null);
            List<ItemExtendedDto> itemDtoList = List.of(firstIemDto, secondItemDto);

            when(itemService.findByOwnerId(eq(userId)))
                    .thenReturn(itemDtoList);

            mvc.perform(get("/items")
                            .header(HttpHeader.USER_ID, userId)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].id", is(firstItemId), Long.class))
                    .andExpect(jsonPath("$[0].name", is(firstIemDto.name())))
                    .andExpect(jsonPath("$[0].description", is(firstIemDto.description())))
                    .andExpect(jsonPath("$[0].available", is(firstIemDto.available())))
                    .andExpect(jsonPath("$[1].id", is(secondItemId), Long.class))
                    .andExpect(jsonPath("$[1].name", is(secondItemDto.name())))
                    .andExpect(jsonPath("$[1].description", is(secondItemDto.description())))
                    .andExpect(jsonPath("$[1].available", is(secondItemDto.available())));

            verify(itemService).findByOwnerId(eq(userId));
        }

        @Test
        @DisplayName("GET /items/search → should return 200 with available items matching query")
        void searchItems_withQuery_shouldReturnMatchingItems() throws Exception {
            String searchQuery = "drill";

            List<ItemDto> searchResults = List.of(
                    new ItemDto(1L, "Electric Drill", "Powerful drill for construction", true),
                    new ItemDto(2L, "Cordless Drill", "Battery-powered drill", true),
                    new ItemDto(3L, "Hammer Drill", "Drill with hammer function", false)
            );

            when(itemService.findByQuery(eq(searchQuery)))
                    .thenReturn(searchResults.stream()
                            .filter(ItemDto::available)
                            .collect(Collectors.toList()));

            mvc.perform(get("/items/search")
                            .param("text", searchQuery)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].id", is(1L), Long.class))
                    .andExpect(jsonPath("$[0].name", is("Electric Drill")))
                    .andExpect(jsonPath("$[0].description", containsString("drill")))
                    .andExpect(jsonPath("$[0].available", is(true)))
                    .andExpect(jsonPath("$[1].id", is(2L), Long.class))
                    .andExpect(jsonPath("$[1].name", is("Cordless Drill")))
                    .andExpect(jsonPath("$[1].available", is(true)));

            verify(itemService).findByQuery(eq(searchQuery));
        }

        @Test
        @DisplayName("DELETE /items/{id} → should delete item and return 204t")
        void deleteItem_byOwner_shouldReturnNoContent() throws Exception {
            Long userId = 1L;
            Long itemId = 4L;

            doNothing().when(itemService).delete(eq(itemId), eq(userId));

            mvc.perform(delete("/items/{id}", itemId)
                            .header(HttpHeader.USER_ID, userId))
                    .andExpect(status().isNoContent())
                    .andExpect(content().string(""));

            verify(itemService).delete(eq(itemId), eq(userId));
        }

        @Test
        @DisplayName("POST /items/{itemId}/comment → should create comment and return 201")
        void createComment_validRequest_shouldReturnCreatedComment() throws Exception {
            Long userId = 1L;
            Long itemId = 4L;
            String commentText = "Great item, very useful!";

            CreateComment request = new CreateComment(commentText);
            CommentDto commentDto = new CommentDto(
                    1L,
                    "John Locke",
                    commentText,
                    LocalDateTime.now()
            );

            when(itemService.createComment(any(CreateComment.class), eq(itemId), eq(userId)))
                    .thenReturn(commentDto);

            mvc.perform(post("/items/{itemId}/comment", itemId)
                            .header(HttpHeader.USER_ID, userId)
                            .content(mapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(commentDto.id()), Long.class))
                    .andExpect(jsonPath("$.text", is(commentText)))
                    .andExpect(jsonPath("$.authorName", is("John Locke")))
                    .andExpect(jsonPath("$.created").exists());

            verify(itemService).createComment(any(CreateComment.class), eq(itemId), eq(userId));
        }
    }

    @Nested
    @DisplayName("Booking endpoints")
    class BookingControllerTests {
        @Mock
        private BookingService bookingService;

        @InjectMocks
        private BookingController bookingController;

        @BeforeEach
        void setup() {
            mvc = MockMvcBuilders
                    .standaloneSetup(bookingController)
                    .setControllerAdvice(new ExceptionController())
                    .build();
        }

        @Test
        @DisplayName("POST /bookings → should create booking and return 201")
        void  createBooking_validRequest_shouldReturnCreatedBooking() throws Exception {
            UserDto booker = new UserDto(1L, "John Locke", "email@email.ru");
            ItemDto item = new ItemDto(5L, "Electric Drill", "Powerful drill for construction", true);
            LocalDateTime start =  LocalDateTime.now().plusDays(1);
            LocalDateTime end =  start.plusDays(3);
            BookingDto bookingDto = new BookingDto(10L, item, booker, start, end, BookingStatus.WAITING);
            CreateBookingRequest request = new CreateBookingRequest(item.id(), start, end);

            when(bookingService.createBooking(any(CreateBookingRequest.class), eq(booker.id())))
                    .thenReturn(bookingDto);

            mvc.perform(post("/bookings")
                            .header(HttpHeader.USER_ID, booker.id())
                            .content(mapper.writeValueAsString(request))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id", is(bookingDto.id()), Long.class))
                    .andExpect(jsonPath("$.start").exists())
                    .andExpect(jsonPath("$.end").exists())
                    .andExpect(jsonPath("$.status", is(BookingStatus.WAITING.toString())))
                    .andExpect(jsonPath("$.item.id", is(item.id()), Long.class))
                    .andExpect(jsonPath("$.item.name", is(item.name())))
                    .andExpect(jsonPath("$.booker.id", is(booker.id()), Long.class))
                    .andExpect(jsonPath("$.booker.name", is(booker.name())));

            verify(bookingService).createBooking(any(CreateBookingRequest.class), eq(booker.id()));
        }

        @Test
        @DisplayName("POST /bookings unavailable item → should return 400")
        void createBooking_unavailableItem_shouldReturnUnavailableException() throws Exception {
            long bookerId = 1L;
            long itemId = 5L;

            LocalDateTime start = LocalDateTime.now().plusDays(1);
            LocalDateTime end = start.plusDays(3);
            CreateBookingRequest request = new CreateBookingRequest(itemId, start, end);

            String errorMessage = "Item id=%d is unavailable".formatted(itemId);

            when(bookingService.createBooking(any(CreateBookingRequest.class), eq(bookerId)))
                    .thenThrow(new UnavailableException(errorMessage));

            mvc.perform(post("/bookings")
                            .header(HttpHeader.USER_ID, bookerId)
                            .content(mapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Resource unavailable"))
                    .andExpect(jsonPath("$.error[0]", is(errorMessage)));

            verify(bookingService).createBooking(any(CreateBookingRequest.class), eq(bookerId));
        }

        @Test
        @DisplayName("PATCH /bookings/{bookingId} → should approve booking and return 200")
        void updateBookingStatus_approveBooking_shouldReturnUpdatedBooking() throws Exception {
            Long ownerId = 1L;
            Long bookingId = 10L;
            boolean approved = true;

            UserDto booker = new UserDto(2L, "John Locke", "email@email.ru");
            ItemDto item = new ItemDto(5L, "Electric Drill", "Powerful drill for construction", true);
            LocalDateTime start = LocalDateTime.now().plusDays(1);
            LocalDateTime end = start.plusDays(3);

            BookingDto updatedBooking = new BookingDto(
                    bookingId,
                    item,
                    booker,
                    start,
                    end,
                    BookingStatus.APPROVED
            );

            when(bookingService.updateBookingStatus(eq(bookingId), eq(approved), eq(ownerId)))
                    .thenReturn(updatedBooking);

            mvc.perform(patch("/bookings/{bookingId}", bookingId)
                            .header(HttpHeader.USER_ID, ownerId.toString())
                            .param("approved", String.valueOf(approved))
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(bookingId), Long.class))
                    .andExpect(jsonPath("$.status", is(BookingStatus.APPROVED.toString())))
                    .andExpect(jsonPath("$.item.id", is(item.id()), Long.class))
                    .andExpect(jsonPath("$.booker.id", is(booker.id()), Long.class));

            verify(bookingService).updateBookingStatus(eq(bookingId), eq(approved), eq(ownerId));
        }

        @Test
        @DisplayName("GET /bookings/{bookingId} → should return booking and return 200")
        void getBookingById_validRequest_shouldReturnBooking() throws Exception {
            Long userId = 1L;
            Long bookingId = 10L;

            UserDto booker = new UserDto(2L, "John Locke", "email@email.ru");
            ItemDto item = new ItemDto(5L, "Electric Drill", "Powerful drill for construction", true);
            LocalDateTime start = LocalDateTime.now().plusDays(1);
            LocalDateTime end = start.plusDays(3);

            BookingDto bookingDto = new BookingDto(
                    bookingId,
                    item,
                    booker,
                    start,
                    end,
                    BookingStatus.APPROVED
            );

            when(bookingService.getBookingById(eq(bookingId), eq(userId)))
                    .thenReturn(bookingDto);

            mvc.perform(get("/bookings/{bookingId}", bookingId)
                            .header(HttpHeader.USER_ID, userId.toString())
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(bookingId), Long.class))
                    .andExpect(jsonPath("$.status", is(BookingStatus.APPROVED.toString())))
                    .andExpect(jsonPath("$.item.id", is(item.id()), Long.class))
                    .andExpect(jsonPath("$.item.name", is(item.name())))
                    .andExpect(jsonPath("$.booker.id", is(booker.id()), Long.class))
                    .andExpect(jsonPath("$.booker.name", is(booker.name())))
                    .andExpect(jsonPath("$.start").exists())
                    .andExpect(jsonPath("$.end").exists());

            verify(bookingService).getBookingById(eq(bookingId), eq(userId));
        }

        @Test
        @DisplayName("GET /bookings with WAITING state → should return 200 and booking list")
        void getBookingsByBookerId_waitingState_shouldReturnWaitingBookings() throws Exception {
            Long userId = 1L;
            BookingState state = BookingState.WAITING;

            List<BookingDto> bookings = List.of(
                    new BookingDto(
                            10L,
                            new ItemDto(5L, "Drill", "Powerful drill", true),
                            new UserDto(userId, "John", "email@email.ru"),
                            LocalDateTime.now().plusDays(1),
                            LocalDateTime.now().plusDays(3),
                            BookingStatus.WAITING
                    ),
                    new BookingDto(
                            11L,
                            new ItemDto(6L, "Hammer", "Steel hammer", true),
                            new UserDto(userId, "John", "email@email.ru"),
                            LocalDateTime.now().plusDays(4),
                            LocalDateTime.now().plusDays(6),
                            BookingStatus.WAITING
                    )
            );

            when(bookingService.getBookingsByBookerId(eq(userId), eq(state)))
                    .thenReturn(bookings);

            mvc.perform(get("/bookings")
                            .header(HttpHeader.USER_ID, userId.toString())
                            .param("state", state.toString())
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].id", is(10L), Long.class))
                    .andExpect(jsonPath("$[0].status", is(BookingStatus.WAITING.toString())))
                    .andExpect(jsonPath("$[0].item.name", is("Drill")))
                    .andExpect(jsonPath("$[1].id", is(11L), Long.class))
                    .andExpect(jsonPath("$[1].status", is(BookingStatus.WAITING.toString())))
                    .andExpect(jsonPath("$[1].item.name", is("Hammer")));

            verify(bookingService).getBookingsByBookerId(eq(userId), eq(state));
        }

        @Test
        @DisplayName("GET /bookings/owner with ALL state → should return 200 and all bookings for owner items")
        void getBookingsByOwnerId_allState_shouldReturnAllBookings() throws Exception {
            Long ownerId = 1L;
            BookingState state = BookingState.ALL;

            List<BookingDto> bookings = List.of(
                    new BookingDto(
                            10L,
                            new ItemDto(5L, "Drill", "Owner's drill", true),
                            new UserDto(2L, "Booker 1", "booker1@email.ru"),
                            LocalDateTime.now().minusDays(2),
                            LocalDateTime.now().minusDays(1),
                            BookingStatus.APPROVED
                    ),
                    new BookingDto(
                            11L,
                            new ItemDto(6L, "Hammer", "Owner's hammer", true),
                            new UserDto(3L, "Booker 2", "booker2@email.ru"),
                            LocalDateTime.now().plusDays(1),
                            LocalDateTime.now().plusDays(3),
                            BookingStatus.WAITING
                    )
            );

            when(bookingService.getBookingsByOwnerId(eq(ownerId), eq(state)))
                    .thenReturn(bookings);

            mvc.perform(get("/bookings/owner")
                            .header(HttpHeader.USER_ID, ownerId.toString())
                            .param("state", state.toString())
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].id", is(10L), Long.class))
                    .andExpect(jsonPath("$[0].item.name", is("Drill")))
                    .andExpect(jsonPath("$[0].booker.name", is("Booker 1")))
                    .andExpect(jsonPath("$[1].id", is(11L), Long.class))
                    .andExpect(jsonPath("$[1].item.name", is("Hammer")))
                    .andExpect(jsonPath("$[1].booker.name", is("Booker 2")));

            verify(bookingService).getBookingsByOwnerId(eq(ownerId), eq(state));
        }

        @Test
        @DisplayName("DELETE /bookings/{bookingId} → should delete booking and return 204")
        void deleteBookingById_validRequest_shouldReturnNoContent() throws Exception {
            Long userId = 1L;
            Long bookingId = 10L;

            doNothing().when(bookingService).deleteBookingById(eq(bookingId), eq(userId));

            mvc.perform(delete("/bookings/{bookingId}", bookingId)
                            .header(HttpHeader.USER_ID, userId.toString()))
                    .andExpect(status().isNoContent())
                    .andExpect(content().string(""));

            verify(bookingService).deleteBookingById(eq(bookingId), eq(userId));
        }
    }

    @Nested
    @DisplayName("ItemRequests endpoints")
    class ItemRequestsControllerTests {
        @Mock
        private ItemRequestServiceImpl  itemRequestService;

        @InjectMocks
        private ItemRequestController itemRequestController;

        @BeforeEach
        void setUp() {
            mvc = MockMvcBuilders
                    .standaloneSetup(itemRequestController)
                    .setControllerAdvice(new ExceptionController())
                    .build();
        }

        @Test
        @DisplayName("POST /requests → should create item request and return 201")
        void createItemRequest_validRequest_shouldReturnCreatedRequest() throws Exception {
            long authorId = 1L;
            String description = "Need a power drill for home repairs";
            LocalDateTime created = LocalDateTime.now();
            CreateItemRequest request = new CreateItemRequest(description);
            ItemRequestDto itemRequestDto = new ItemRequestDto(
                    10L,
                    description,
                    created
            );

            when(itemRequestService.create(eq(authorId), any(CreateItemRequest.class)))
                    .thenReturn(itemRequestDto);

            mvc.perform(post("/requests")
                            .header(HttpHeader.USER_ID, authorId)
                            .content(mapper.writeValueAsString(request))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(itemRequestDto.id()), Long.class))
                    .andExpect(jsonPath("$.description", is(description)))
                    .andExpect(jsonPath("$.created").exists());

            verify(itemRequestService).create(eq(authorId), any(CreateItemRequest.class));
        }

        @Test
        @DisplayName("GET /requests → should return item requests by author")
        void getItemRequestByAuthor_validRequest_shouldReturnRequests() throws Exception {
            Long authorId = 1L;
            List<ItemRequestExtendedDto> requests = List.of(
                    new ItemRequestExtendedDto(
                            10L,
                            "Need a power drill",
                            LocalDateTime.now().minusDays(2),
                            Collections.emptyList()
                    ),
                    new ItemRequestExtendedDto(
                            11L,
                            "Looking for a hammer",
                            LocalDateTime.now().minusDays(1),
                            List.of(
                                    new ItemDto(5L, "Hammer", "Steel hammer", true)
                            )
                    )
            );

            when(itemRequestService.getAllByAuthor(eq(authorId)))
                    .thenReturn(requests);

            mvc.perform(get("/requests")
                            .header(HttpHeader.USER_ID, authorId)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].id", is(10L), Long.class))
                    .andExpect(jsonPath("$[0].description", is("Need a power drill")))
                    .andExpect(jsonPath("$[0].items").isArray())
                    .andExpect(jsonPath("$[0].items.length()").value(0))
                    .andExpect(jsonPath("$[1].id", is(11L), Long.class))
                    .andExpect(jsonPath("$[1].description", is("Looking for a hammer")))
                    .andExpect(jsonPath("$[1].items[0].id", is(5L), Long.class))
                    .andExpect(jsonPath("$[1].items[0].name", is("Hammer")));

            verify(itemRequestService).getAllByAuthor(eq(authorId));
        }

        @Test
        @DisplayName("GET /requests/all → should return all item requests")
        void getAllItemRequests_shouldReturnAllRequests() throws Exception {
            List<ItemRequestDto> allRequests = List.of(
                    new ItemRequestDto(10L, "Need drill", LocalDateTime.now().minusDays(3)),
                    new ItemRequestDto(11L, "Need saw", LocalDateTime.now().minusDays(2)),
                    new ItemRequestDto(12L, "Need hammer", LocalDateTime.now().minusDays(1))
            );

            when(itemRequestService.getAll())
                    .thenReturn(allRequests);

            mvc.perform(get("/requests/all")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(3))
                    .andExpect(jsonPath("$[0].id", is(10L), Long.class))
                    .andExpect(jsonPath("$[0].description", is("Need drill")))
                    .andExpect(jsonPath("$[1].id", is(11L), Long.class))
                    .andExpect(jsonPath("$[1].description", is("Need saw")))
                    .andExpect(jsonPath("$[2].id", is(12L), Long.class))
                    .andExpect(jsonPath("$[2].description", is("Need hammer")));

            verify(itemRequestService).getAll();
        }

        @Test
        @DisplayName("GET /requests/{requestId} → should return item request by ID")
        void getItemRequest_validId_shouldReturnRequest() throws Exception {
            Long requestId = 10L;
            ItemRequestExtendedDto request = new ItemRequestExtendedDto(
                    requestId,
                    "Need a cordless drill for home repairs",
                    LocalDateTime.now().minusDays(2),
                    List.of(
                            new ItemDto(5L, "Cordless Drill", "18V battery drill", true),
                            new ItemDto(6L, "Electric Drill", "Wired power drill", false)
                    )
            );

            when(itemRequestService.getById(eq(requestId)))
                    .thenReturn(request);

            mvc.perform(get("/requests/{requestId}", requestId)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(requestId), Long.class))
                    .andExpect(jsonPath("$.description", is("Need a cordless drill for home repairs")))
                    .andExpect(jsonPath("$.created").exists())
                    .andExpect(jsonPath("$.items").isArray())
                    .andExpect(jsonPath("$.items.length()").value(2))
                    .andExpect(jsonPath("$.items[0].id", is(5L), Long.class))
                    .andExpect(jsonPath("$.items[0].name", is("Cordless Drill")))
                    .andExpect(jsonPath("$.items[0].available", is(true)))
                    .andExpect(jsonPath("$.items[1].id", is(6L), Long.class))
                    .andExpect(jsonPath("$.items[1].name", is("Electric Drill")))
                    .andExpect(jsonPath("$.items[1].available", is(false)));

            verify(itemRequestService).getById(eq(requestId));
        }
    }
}
