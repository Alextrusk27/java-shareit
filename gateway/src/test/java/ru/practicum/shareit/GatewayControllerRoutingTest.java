package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.client.HttpHeader;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CreateComment;
import ru.practicum.shareit.item.dto.CreateItem;
import ru.practicum.shareit.item.dto.UpdateItem;
import ru.practicum.shareit.request.RequestClient;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.CreateItemRequest;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("Controller Routing Tests")
class GatewayControllerRoutingTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("User Controller Routing Test")
    class UserControllerRoutingTest {

        @Mock
        private UserClient userClient;

        @InjectMocks
        private UserController userController;

        @BeforeEach
        void setUp() {
            mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
            objectMapper = new ObjectMapper();
        }

        @Test
        @DisplayName("POST /users -> should route to userClient.createUser()")
        void createUser_shouldRouteToCreateUser() throws Exception {
            CreateUserRequest request = new CreateUserRequest("John", "john@example.com");
            when(userClient.createUser(any())).thenReturn(ResponseEntity.ok().build());

            mockMvc.perform(post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            verify(userClient).createUser(any(CreateUserRequest.class));
        }

        @Test
        @DisplayName("PATCH /users/{userId} -> should route to userClient.updateUser() with correct ID")
        void updateUser_shouldRouteToUpdateUserWithCorrectId() throws Exception {
            long userId = 1L;
            UpdateUserRequest request = new UpdateUserRequest("Updated", "updated@example.com");
            when(userClient.updateUser(eq(userId), any())).thenReturn(ResponseEntity.ok().build());

            mockMvc.perform(patch("/users/{userId}", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            verify(userClient).updateUser(eq(userId), any(UpdateUserRequest.class));
        }

        @Test
        @DisplayName("GET /users/{userId} -> should route to userClient.getUser() with correct ID")
        void getUserById_shouldRouteToGetUserWithCorrectId() throws Exception {
            long userId = 1L;
            when(userClient.getUser(eq(userId))).thenReturn(ResponseEntity.ok().build());

            mockMvc.perform(get("/users/{userId}", userId))
                    .andExpect(status().isOk());

            verify(userClient).getUser(eq(userId));
        }

        @Test
        @DisplayName("GET /users/{userId} -> should work with different IDs")
        void getUserById_withDifferentIds_shouldRouteCorrectly() throws Exception {
            long userId = 999L;
            when(userClient.getUser(eq(userId))).thenReturn(ResponseEntity.ok().build());

            mockMvc.perform(get("/users/{userId}", userId))
                    .andExpect(status().isOk());

            verify(userClient).getUser(eq(userId));
        }

        @Test
        @DisplayName("DELETE /users/{userId} -> should route to userClient.deleteUser() with correct ID")
        void deleteUser_shouldRouteToDeleteUserWithCorrectId() throws Exception {
            long userId = 1L;
            when(userClient.deleteUser(eq(userId))).thenReturn(ResponseEntity.ok().build());

            mockMvc.perform(delete("/users/{userId}", userId))
                    .andExpect(status().isOk());

            verify(userClient).deleteUser(eq(userId));
        }

        @Test
        @DisplayName("DELETE /users/{userId} -> should work with different IDs")
        void deleteUser_withDifferentIds_shouldRouteCorrectly() throws Exception {
            long userId = 500L;
            when(userClient.deleteUser(eq(userId))).thenReturn(ResponseEntity.ok().build());

            mockMvc.perform(delete("/users/{userId}", userId))
                    .andExpect(status().isOk());

            verify(userClient).deleteUser(eq(userId));
        }

        @Test
        @DisplayName("PATCH /users/{userId} -> should preserve request body content")
        void updateUser_shouldPreserveRequestBody() throws Exception {
            long userId = 1L;
            UpdateUserRequest expectedRequest = new UpdateUserRequest("John Updated", "john.updated@example.com");
            when(userClient.updateUser(eq(userId), any())).thenReturn(ResponseEntity.ok().build());

            mockMvc.perform(patch("/users/{userId}", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(expectedRequest)))
                    .andExpect(status().isOk());

            verify(userClient).updateUser(eq(userId), argThat(actualRequest ->
                    actualRequest.name().equals(expectedRequest.name()) &&
                            actualRequest.email().equals(expectedRequest.email())
            ));
        }

        @Test
        @DisplayName("POST /users -> should preserve request body content")
        void createUser_shouldPreserveRequestBody() throws Exception {
            CreateUserRequest expectedRequest = new CreateUserRequest("Alice", "alice@example.com");
            when(userClient.createUser(any())).thenReturn(ResponseEntity.ok().build());

            mockMvc.perform(post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(expectedRequest)))
                    .andExpect(status().isOk());

            verify(userClient).createUser(argThat(actualRequest ->
                    actualRequest.name().equals(expectedRequest.name()) &&
                            actualRequest.email().equals(expectedRequest.email())
            ));
        }

        @Test
        @DisplayName("Wrong HTTP method -> should return 405")
        void wrongHttpMethod_shouldReturnMethodNotAllowed() throws Exception {
            mockMvc.perform(put("/users/1"))
                    .andExpect(status().isMethodNotAllowed());
        }

        @Test
        @DisplayName("Non-existent endpoint -> should return 404")
        void nonExistentEndpoint_shouldReturnNotFound() throws Exception {
            mockMvc.perform(get("/users/1/invalid"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("PATCH /users/{userId} -> endpoint should be accessible")
        void updateUser_endpointShouldBeAccessible() throws Exception {
            long userId = 1L;
            UpdateUserRequest request = new UpdateUserRequest("Test", "test@example.com");
            when(userClient.updateUser(eq(userId), any())).thenReturn(ResponseEntity.ok().build());

            mockMvc.perform(patch("/users/{userId}", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            verify(userClient).updateUser(eq(userId), any());
        }
    }

    @Nested
    @DisplayName("Item Controller Routing Test")
    class ItemControllerRoutingTest {

        @Mock
        private ItemClient itemClient;

        @InjectMocks
        private ItemController itemController;

        @BeforeEach
        void setUp() {
            mockMvc = MockMvcBuilders.standaloneSetup(itemController).build();
            objectMapper = new ObjectMapper();
        }

        @Test
        @DisplayName("POST /items -> should route to itemClient.create() with user ID header")
        void createItem_shouldRouteToCreateWithUserIdHeader() throws Exception {
            long userId = 1L;
            CreateItem request = new CreateItem("Drill", "Powerful drill", true, 123L);
            when(itemClient.create(eq(userId), any())).thenReturn(ResponseEntity.ok().build());

            mockMvc.perform(post("/items")
                            .header(HttpHeader.USER_ID, userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            verify(itemClient).create(eq(userId), any(CreateItem.class));
        }

        @Test
        @DisplayName("PATCH /items/{id} -> should route to itemClient.update() with correct parameters")
        void updateItem_shouldRouteToUpdateWithCorrectParameters() throws Exception {
            long userId = 1L;
            long itemId = 2L;
            UpdateItem request = new UpdateItem("Updated Drill", "More powerful", false);
            when(itemClient.update(eq(userId), eq(itemId), any())).thenReturn(ResponseEntity.ok().build());

            mockMvc.perform(patch("/items/{id}", itemId)
                            .header(HttpHeader.USER_ID, userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            verify(itemClient).update(eq(userId), eq(itemId), any(UpdateItem.class));
        }

        @Test
        @DisplayName("GET /items/{id} -> should route to itemClient.getById() with correct parameters")
        void getItemById_shouldRouteToGetByIdWithCorrectParameters() throws Exception {
            long userId = 1L;
            long itemId = 2L;
            when(itemClient.getById(eq(userId), eq(itemId))).thenReturn(ResponseEntity.ok().build());

            mockMvc.perform(get("/items/{id}", itemId)
                            .header(HttpHeader.USER_ID, userId))
                    .andExpect(status().isOk());

            verify(itemClient).getById(eq(userId), eq(itemId));
        }

        @Test
        @DisplayName("GET /items -> should route to itemClient.getAll() with user ID header")
        void getOwnItems_shouldRouteToGetAllWithUserIdHeader() throws Exception {
            long userId = 1L;
            when(itemClient.getAll(eq(userId))).thenReturn(ResponseEntity.ok().build());

            mockMvc.perform(get("/items")
                            .header(HttpHeader.USER_ID, userId))
                    .andExpect(status().isOk());

            verify(itemClient).getAll(eq(userId));
        }

        @Test
        @DisplayName("GET /items/search -> should route to itemClient.getByQuery() with text parameter")
        void searchItems_shouldRouteToGetByQueryWithTextParameter() throws Exception {
            long userId = 1L;
            String searchText = "drill";
            when(itemClient.getByQuery(eq(userId), eq(searchText))).thenReturn(ResponseEntity.ok().build());

            mockMvc.perform(get("/items/search")
                            .header(HttpHeader.USER_ID, userId)
                            .param("text", searchText))
                    .andExpect(status().isOk());

            verify(itemClient).getByQuery(eq(userId), eq(searchText));
        }

        @Test
        @DisplayName("DELETE /items/{id} -> should route to itemClient.delete() with correct parameters")
        void deleteItem_shouldRouteToDeleteWithCorrectParameters() throws Exception {
            long userId = 1L;
            long itemId = 2L;
            when(itemClient.delete(eq(userId), eq(itemId))).thenReturn(ResponseEntity.ok().build());

            mockMvc.perform(delete("/items/{id}", itemId)
                            .header(HttpHeader.USER_ID, userId))
                    .andExpect(status().isOk());

            verify(itemClient).delete(eq(userId), eq(itemId));
        }

        @Test
        @DisplayName("POST /items/{itemId}/comment -> should route to itemClient.createComment() with correct parameters")
        void createComment_shouldRouteToCreateCommentWithCorrectParameters() throws Exception {
            long userId = 1L;
            long itemId = 2L;
            CreateComment request = new CreateComment("Great item!");
            when(itemClient.createComment(eq(userId), eq(itemId), any())).thenReturn(ResponseEntity.ok().build());

            mockMvc.perform(post("/items/{itemId}/comment", itemId)
                            .header(HttpHeader.USER_ID, userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            verify(itemClient).createComment(eq(userId), eq(itemId), any(CreateComment.class));
        }

        @Test
        @DisplayName("POST /items -> should preserve request body content")
        void createItem_shouldPreserveRequestBody() throws Exception {
            long userId = 1L;
            CreateItem expectedRequest = new CreateItem("Hammer", "Heavy hammer", false, null);
            when(itemClient.create(eq(userId), any())).thenReturn(ResponseEntity.ok().build());

            mockMvc.perform(post("/items")
                            .header(HttpHeader.USER_ID, userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(expectedRequest)))
                    .andExpect(status().isOk());

            verify(itemClient).create(eq(userId), argThat(actualRequest ->
                    actualRequest.name().equals(expectedRequest.name()) &&
                            actualRequest.description().equals(expectedRequest.description()) &&
                            actualRequest.available() == expectedRequest.available() &&
                            Objects.equals(actualRequest.requestId(), expectedRequest.requestId())
            ));
        }

        @Test
        @DisplayName("PATCH /items/{id} -> should preserve request body content")
        void updateItem_shouldPreserveRequestBody() throws Exception {
            long userId = 1L;
            long itemId = 2L;
            UpdateItem expectedRequest = new UpdateItem("Updated Name", "Updated description", true);
            when(itemClient.update(eq(userId), eq(itemId), any())).thenReturn(ResponseEntity.ok().build());

            mockMvc.perform(patch("/items/{id}", itemId)
                            .header(HttpHeader.USER_ID, userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(expectedRequest)))
                    .andExpect(status().isOk());

            verify(itemClient).update(eq(userId), eq(itemId), argThat(actualRequest ->
                    actualRequest.name().equals(expectedRequest.name()) &&
                            actualRequest.description().equals(expectedRequest.description()) &&
                            actualRequest.available() == expectedRequest.available()
            ));
        }

        @Test
        @DisplayName("POST /items/{itemId}/comment -> should preserve comment text")
        void createComment_shouldPreserveCommentText() throws Exception {
            long userId = 1L;
            long itemId = 2L;
            CreateComment expectedRequest = new CreateComment("Very useful, thank you!");
            when(itemClient.createComment(eq(userId), eq(itemId), any())).thenReturn(ResponseEntity.ok().build());

            mockMvc.perform(post("/items/{itemId}/comment", itemId)
                            .header(HttpHeader.USER_ID, userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(expectedRequest)))
                    .andExpect(status().isOk());

            verify(itemClient).createComment(eq(userId), eq(itemId), argThat(actualRequest ->
                    actualRequest.text().equals(expectedRequest.text())
            ));
        }

        @Test
        @DisplayName("GET /items/search -> should preserve query parameter")
        void searchItems_shouldPreserveQueryParameter() throws Exception {
            long userId = 1L;
            String expectedQuery = "drill hammer screwdriver";
            when(itemClient.getByQuery(eq(userId), eq(expectedQuery))).thenReturn(ResponseEntity.ok().build());

            mockMvc.perform(get("/items/search")
                            .header(HttpHeader.USER_ID, userId)
                            .param("text", expectedQuery))
                    .andExpect(status().isOk());

            verify(itemClient).getByQuery(eq(userId), eq(expectedQuery));
        }

        @Test
        @DisplayName("Wrong HTTP method for /items -> should return 405")
        void wrongHttpMethodForItems_shouldReturnMethodNotAllowed() throws Exception {
            mockMvc.perform(put("/items")
                            .header(HttpHeader.USER_ID, 1L))
                    .andExpect(status().isMethodNotAllowed());
        }

        @Test
        @DisplayName("Non-existent endpoint -> should return 404")
        void nonExistentEndpoint_shouldReturnNotFound() throws Exception {
            mockMvc.perform(get("/items/1/comments/invalid")
                            .header(HttpHeader.USER_ID, 1L))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("GET /items/{id} -> should work with different IDs")
        void getItemById_withDifferentIds_shouldRouteCorrectly() throws Exception {
            long userId = 1L;
            long itemId = 999L;
            when(itemClient.getById(eq(userId), eq(itemId))).thenReturn(ResponseEntity.ok().build());

            mockMvc.perform(get("/items/{id}", itemId)
                            .header(HttpHeader.USER_ID, userId))
                    .andExpect(status().isOk());

            verify(itemClient).getById(eq(userId), eq(itemId));
        }

        @Test
        @DisplayName("GET /items/search -> with empty query -> should route correctly")
        void searchItems_withEmptyQuery_shouldRouteCorrectly() throws Exception {
            long userId = 1L;
            String query = "";
            when(itemClient.getByQuery(eq(userId), eq(query))).thenReturn(ResponseEntity.ok().build());

            mockMvc.perform(get("/items/search")
                            .header(HttpHeader.USER_ID, userId)
                            .param("text", query))
                    .andExpect(status().isOk());

            verify(itemClient).getByQuery(eq(userId), eq(query));
        }

        @Test
        @DisplayName("GET /items/search -> with special characters in query -> should route correctly")
        void searchItems_withSpecialCharacters_shouldRouteCorrectly() throws Exception {
            long userId = 1L;
            String query = "drill & hammer (heavy)";
            when(itemClient.getByQuery(eq(userId), eq(query))).thenReturn(ResponseEntity.ok().build());

            mockMvc.perform(get("/items/search")
                            .header(HttpHeader.USER_ID, userId)
                            .param("text", query))
                    .andExpect(status().isOk());

            verify(itemClient).getByQuery(eq(userId), eq(query));
        }
    }

    @Nested
    @DisplayName("Booking Controller Routing Test")
    class BookingControllerRoutingTest {

        @Mock
        private BookingClient bookingClient;

        @InjectMocks
        private BookingController bookingController;

        @BeforeEach
        void setUp() {
            mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();
            objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
        }

        @Test
        @DisplayName("POST /bookings -> should route to bookingClient.bookItem()")
        void createBooking_shouldRouteToBookItem() throws Exception {
            long bookerId = 1L;
            CreateBookingRequest request = new CreateBookingRequest(
                    2L,
                    LocalDateTime.now().plusDays(1),
                    LocalDateTime.now().plusDays(2)
            );
            when(bookingClient.bookItem(eq(bookerId), any())).thenReturn(ResponseEntity.ok().build());

            mockMvc.perform(post("/bookings")
                            .header(HttpHeader.USER_ID, bookerId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            verify(bookingClient).bookItem(eq(bookerId), any(CreateBookingRequest.class));
        }

        @Test
        @DisplayName("PATCH /bookings/{bookingId} -> should route to bookingClient.updateBooking()")
        void updateBookingStatus_shouldRouteToUpdateBooking() throws Exception {
            long userId = 1L;
            long bookingId = 2L;
            boolean approved = true;
            when(bookingClient.updateBooking(eq(userId), eq(bookingId), eq(approved)))
                    .thenReturn(ResponseEntity.ok().build());

            mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                            .header(HttpHeader.USER_ID, userId)
                            .param("approved", String.valueOf(approved)))
                    .andExpect(status().isOk());

            verify(bookingClient).updateBooking(eq(userId), eq(bookingId), eq(approved));
        }

        @Test
        @DisplayName("GET /bookings/{bookingId} -> should route to bookingClient.getBooking()")
        void getBookingById_shouldRouteToGetBooking() throws Exception {
            long userId = 1L;
            long bookingId = 2L;
            when(bookingClient.getBooking(eq(userId), eq(bookingId))).thenReturn(ResponseEntity.ok().build());

            mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                            .header(HttpHeader.USER_ID, userId))
                    .andExpect(status().isOk());

            verify(bookingClient).getBooking(eq(userId), eq(bookingId));
        }

        @Test
        @DisplayName("GET /bookings -> should route to bookingClient.getBookings() with default parameters")
        void getBookings_shouldRouteToGetBookingsWithDefaults() throws Exception {
            long userId = 1L;
            when(bookingClient.getBookings(eq(userId), eq(BookingState.ALL), eq(0), eq(10)))
                    .thenReturn(ResponseEntity.ok().build());

            mockMvc.perform(get("/bookings")
                            .header(HttpHeader.USER_ID, userId))
                    .andExpect(status().isOk());

            verify(bookingClient).getBookings(eq(userId), eq(BookingState.ALL), eq(0), eq(10));
        }

        @Test
        @DisplayName("GET /bookings -> should route with custom parameters")
        void getBookings_withCustomParameters_shouldRouteCorrectly() throws Exception {
            long userId = 1L;
            String state = "WAITING";
            int from = 5;
            int size = 20;
            when(bookingClient.getBookings(eq(userId), eq(BookingState.WAITING), eq(from), eq(size)))
                    .thenReturn(ResponseEntity.ok().build());

            mockMvc.perform(get("/bookings")
                            .header(HttpHeader.USER_ID, userId)
                            .param("state", state)
                            .param("from", String.valueOf(from))
                            .param("size", String.valueOf(size)))
                    .andExpect(status().isOk());

            verify(bookingClient).getBookings(eq(userId), eq(BookingState.WAITING), eq(from), eq(size));
        }

        @Test
        @DisplayName("GET /bookings/owner -> should route to bookingClient.getBookingsByOwner() with default params")
        void getBookingsByOwner_shouldRouteToGetBookingsByOwnerWithDefaults() throws Exception {
            long userId = 1L;
            when(bookingClient.getBookingsByOwner(eq(userId), eq(BookingState.ALL), eq(0), eq(10)))
                    .thenReturn(ResponseEntity.ok().build());

            mockMvc.perform(get("/bookings/owner")
                            .header(HttpHeader.USER_ID, userId))
                    .andExpect(status().isOk());

            verify(bookingClient).getBookingsByOwner(eq(userId), eq(BookingState.ALL), eq(0), eq(10));
        }

        @Test
        @DisplayName("GET /bookings/owner -> should route with custom parameters")
        void getBookingsByOwner_withCustomParameters_shouldRouteCorrectly() throws Exception {
            long userId = 1L;
            String state = "REJECTED";
            int from = 10;
            int size = 50;
            when(bookingClient.getBookingsByOwner(eq(userId), eq(BookingState.REJECTED), eq(from), eq(size)))
                    .thenReturn(ResponseEntity.ok().build());

            mockMvc.perform(get("/bookings/owner")
                            .header(HttpHeader.USER_ID, userId)
                            .param("state", state)
                            .param("from", String.valueOf(from))
                            .param("size", String.valueOf(size)))
                    .andExpect(status().isOk());

            verify(bookingClient).getBookingsByOwner(eq(userId), eq(BookingState.REJECTED), eq(from), eq(size));
        }

        @Test
        @DisplayName("DELETE /bookings/{bookingId} -> should route to bookingClient.deleteBooking()")
        void deleteBooking_shouldRouteToDeleteBooking() throws Exception {
            long userId = 1L;
            long bookingId = 2L;
            when(bookingClient.deleteBooking(eq(userId), eq(bookingId))).thenReturn(ResponseEntity.ok().build());

            mockMvc.perform(delete("/bookings/{bookingId}", bookingId)
                            .header(HttpHeader.USER_ID, userId))
                    .andExpect(status().isOk());

            verify(bookingClient).deleteBooking(eq(userId), eq(bookingId));
        }

        @Test
        @DisplayName("PATCH /bookings/{bookingId} -> should preserve approved parameter")
        void updateBookingStatus_shouldPreserveApprovedParameter() throws Exception {
            long userId = 1L;
            long bookingId = 2L;
            boolean approved = false;
            when(bookingClient.updateBooking(eq(userId), eq(bookingId), eq(approved)))
                    .thenReturn(ResponseEntity.ok().build());

            mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                            .header(HttpHeader.USER_ID, userId)
                            .param("approved", String.valueOf(approved)))
                    .andExpect(status().isOk());

            verify(bookingClient).updateBooking(eq(userId), eq(bookingId), eq(approved));
        }

        @Test
        @DisplayName("GET /bookings -> with all state values should route correctly")
        void getBookings_withDifferentStates_shouldRouteCorrectly() throws Exception {
            long userId = 1L;
            Map<String, BookingState> states = Map.of(
                    "all", BookingState.ALL,
                    "current", BookingState.CURRENT,
                    "past", BookingState.PAST,
                    "future", BookingState.FUTURE,
                    "waiting", BookingState.WAITING,
                    "rejected", BookingState.REJECTED
            );

            for (Map.Entry<String, BookingState> entry : states.entrySet()) {
                String stateParam = entry.getKey();
                BookingState state = entry.getValue();
                when(bookingClient.getBookings(eq(userId), eq(state), eq(0), eq(10)))
                        .thenReturn(ResponseEntity.ok().build());

                mockMvc.perform(get("/bookings")
                                .header(HttpHeader.USER_ID, userId)
                                .param("state", stateParam))
                        .andExpect(status().isOk());

                verify(bookingClient).getBookings(eq(userId), eq(state), eq(0), eq(10));
                reset(bookingClient);
            }
        }

        @Test
        @DisplayName("GET /bookings/owner -> with all state values should route correctly")
        void getBookingsByOwner_withDifferentStates_shouldRouteCorrectly() throws Exception {
            long userId = 1L;
            Map<String, BookingState> states = Map.of(
                    "all", BookingState.ALL,
                    "current", BookingState.CURRENT,
                    "past", BookingState.PAST,
                    "future", BookingState.FUTURE,
                    "waiting", BookingState.WAITING,
                    "rejected", BookingState.REJECTED
            );

            for (Map.Entry<String, BookingState> entry : states.entrySet()) {
                String stateParam = entry.getKey();
                BookingState state = entry.getValue();
                when(bookingClient.getBookingsByOwner(eq(userId), eq(state), eq(0), eq(10)))
                        .thenReturn(ResponseEntity.ok().build());

                mockMvc.perform(get("/bookings/owner")
                                .header(HttpHeader.USER_ID, userId)
                                .param("state", stateParam))
                        .andExpect(status().isOk());

                verify(bookingClient).getBookingsByOwner(eq(userId), eq(state), eq(0), eq(10));
                reset(bookingClient);
            }
        }

        @Test
        @DisplayName("Wrong HTTP method -> should return 405")
        void wrongHttpMethod_shouldReturnMethodNotAllowed() throws Exception {
            mockMvc.perform(put("/bookings/1")
                            .header(HttpHeader.USER_ID, 1L))
                    .andExpect(status().isMethodNotAllowed());
        }

        @Test
        @DisplayName("Non-existent endpoint -> should return 404")
        void nonExistentEndpoint_shouldReturnNotFound() throws Exception {
            mockMvc.perform(get("/bookings/1/invalid")
                            .header(HttpHeader.USER_ID, 1L))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("GET /bookings/{bookingId} -> should work with different IDs")
        void getBookingById_withDifferentIds_shouldRouteCorrectly() throws Exception {
            long userId = 1L;
            long bookingId = 999L;
            when(bookingClient.getBooking(eq(userId), eq(bookingId))).thenReturn(ResponseEntity.ok().build());

            mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                            .header(HttpHeader.USER_ID, userId))
                    .andExpect(status().isOk());

            verify(bookingClient).getBooking(eq(userId), eq(bookingId));
        }

        @Test
        @DisplayName("PATCH /bookings/{bookingId} -> should work with different IDs")
        void updateBookingStatus_withDifferentIds_shouldRouteCorrectly() throws Exception {
            long userId = 1L;
            long bookingId = 500L;
            boolean approved = true;
            when(bookingClient.updateBooking(eq(userId), eq(bookingId), eq(approved)))
                    .thenReturn(ResponseEntity.ok().build());

            mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                            .header(HttpHeader.USER_ID, userId)
                            .param("approved", String.valueOf(approved)))
                    .andExpect(status().isOk());

            verify(bookingClient).updateBooking(eq(userId), eq(bookingId), eq(approved));
        }
    }

    @Nested
    @DisplayName("Item Request Controller Routing Test")
    class ItemRequestControllerRoutingTest {

        @Mock
        private RequestClient requestClient;

        @InjectMocks
        private ItemRequestController itemRequestController;

        @BeforeEach
        void setUp() {
            mockMvc = MockMvcBuilders.standaloneSetup(itemRequestController).build();
            objectMapper = new ObjectMapper();
        }

        @Test
        @DisplayName("POST /requests -> should route to requestClient.create()")
        void createItemRequest_shouldRouteToCreate() throws Exception {
            long userId = 1L;
            CreateItemRequest request = new CreateItemRequest("Need a power drill");
            when(requestClient.create(eq(userId), any())).thenReturn(ResponseEntity.ok().build());

            mockMvc.perform(post("/requests")
                            .header(HttpHeader.USER_ID, userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            verify(requestClient).create(eq(userId), any(CreateItemRequest.class));
        }

        @Test
        @DisplayName("GET /requests -> should route to requestClient.getByUser()")
        void getItemRequestByAuthor_shouldRouteToGetByUser() throws Exception {
            long userId = 1L;
            when(requestClient.getByUser(eq(userId))).thenReturn(ResponseEntity.ok().build());

            mockMvc.perform(get("/requests")
                            .header(HttpHeader.USER_ID, userId))
                    .andExpect(status().isOk());

            verify(requestClient).getByUser(eq(userId));
        }

        @Test
        @DisplayName("GET /requests/all -> should route to requestClient.getAll()")
        void getAllItemRequests_shouldRouteToGetAll() throws Exception {
            when(requestClient.getAll()).thenReturn(ResponseEntity.ok().build());

            mockMvc.perform(get("/requests/all"))
                    .andExpect(status().isOk());

            verify(requestClient).getAll();
        }

        @Test
        @DisplayName("GET /requests/{requestId} -> should route to requestClient.getById()")
        void getItemRequest_shouldRouteToGetById() throws Exception {
            long requestId = 1L;
            when(requestClient.getById(eq(requestId))).thenReturn(ResponseEntity.ok().build());

            mockMvc.perform(get("/requests/{requestId}", requestId))
                    .andExpect(status().isOk());

            verify(requestClient).getById(eq(requestId));
        }

        @Test
        @DisplayName("POST /requests -> should preserve request body content")
        void createItemRequest_shouldPreserveRequestBody() throws Exception {
            long userId = 1L;
            CreateItemRequest expectedRequest = new CreateItemRequest("Looking for camping equipment");
            when(requestClient.create(eq(userId), any())).thenReturn(ResponseEntity.ok().build());

            mockMvc.perform(post("/requests")
                            .header(HttpHeader.USER_ID, userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(expectedRequest)))
                    .andExpect(status().isOk());

            verify(requestClient).create(eq(userId), argThat(actualRequest ->
                    actualRequest.description().equals(expectedRequest.description())
            ));
        }

        @Test
        @DisplayName("GET /requests/{requestId} -> should work with different IDs")
        void getItemRequest_withDifferentIds_shouldRouteCorrectly() throws Exception {
            long requestId = 999L;
            when(requestClient.getById(eq(requestId))).thenReturn(ResponseEntity.ok().build());

            mockMvc.perform(get("/requests/{requestId}", requestId))
                    .andExpect(status().isOk());

            verify(requestClient).getById(eq(requestId));
        }

        @Test
        @DisplayName("GET /requests -> should work with different user IDs")
        void getItemRequestByAuthor_withDifferentUserIds_shouldRouteCorrectly() throws Exception {
            long userId = 500L;
            when(requestClient.getByUser(eq(userId))).thenReturn(ResponseEntity.ok().build());

            mockMvc.perform(get("/requests")
                            .header(HttpHeader.USER_ID, userId))
                    .andExpect(status().isOk());

            verify(requestClient).getByUser(eq(userId));
        }

        @Test
        @DisplayName("POST /requests -> should work with different user IDs")
        void createItemRequest_withDifferentUserIds_shouldRouteCorrectly() throws Exception {
            long userId = 300L;
            CreateItemRequest request = new CreateItemRequest("Need gardening tools");
            when(requestClient.create(eq(userId), any())).thenReturn(ResponseEntity.ok().build());

            mockMvc.perform(post("/requests")
                            .header(HttpHeader.USER_ID, userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            verify(requestClient).create(eq(userId), any(CreateItemRequest.class));
        }

        @Test
        @DisplayName("Wrong HTTP method -> should return 405")
        void wrongHttpMethod_shouldReturnMethodNotAllowed() throws Exception {
            mockMvc.perform(put("/requests")
                            .header(HttpHeader.USER_ID, 1L))
                    .andExpect(status().isMethodNotAllowed());
        }

        @Test
        @DisplayName("Non-existent endpoint -> should return 404")
        void nonExistentEndpoint_shouldReturnNotFound() throws Exception {
            mockMvc.perform(get("/requests/1/details")
                            .header(HttpHeader.USER_ID, 1L))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("GET /requests/all -> should be accessible without authentication")
        void getAllItemRequests_shouldBeAccessibleWithoutAuth() throws Exception {
            when(requestClient.getAll()).thenReturn(ResponseEntity.ok().build());

            mockMvc.perform(get("/requests/all"))
                    .andExpect(status().isOk());

            verify(requestClient).getAll();
        }

        @Test
        @DisplayName("POST /requests -> with long description should route correctly")
        void createItemRequest_withLongDescription_shouldRouteCorrectly() throws Exception {
            long userId = 1L;
            String longDescription = "Need a high-quality power drill with hammer function " +
                    "for home renovation projects, preferably cordless with multiple batteries";
            CreateItemRequest request = new CreateItemRequest(longDescription);
            when(requestClient.create(eq(userId), any())).thenReturn(ResponseEntity.ok().build());

            mockMvc.perform(post("/requests")
                            .header(HttpHeader.USER_ID, userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            verify(requestClient).create(eq(userId), argThat(actualRequest ->
                    actualRequest.description().equals(longDescription)
            ));
        }

        @Test
        @DisplayName("Different endpoints should not interfere")
        void differentEndpoints_shouldNotInterfere() throws Exception {
            long userId = 1L;
            long requestId = 2L;

            when(requestClient.getByUser(eq(userId))).thenReturn(ResponseEntity.ok().build());
            when(requestClient.getById(eq(requestId))).thenReturn(ResponseEntity.ok().build());
            when(requestClient.getAll()).thenReturn(ResponseEntity.ok().build());

            mockMvc.perform(get("/requests")
                            .header(HttpHeader.USER_ID, userId))
                    .andExpect(status().isOk());

            mockMvc.perform(get("/requests/all"))
                    .andExpect(status().isOk());

            mockMvc.perform(get("/requests/{requestId}", requestId))
                    .andExpect(status().isOk());

            verify(requestClient).getByUser(eq(userId));
            verify(requestClient).getById(eq(requestId));
            verify(requestClient).getAll();
        }
    }
}