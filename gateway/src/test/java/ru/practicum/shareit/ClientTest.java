package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.dto.CreateComment;
import ru.practicum.shareit.item.dto.CreateItem;
import ru.practicum.shareit.item.dto.UpdateItem;
import ru.practicum.shareit.request.RequestClient;
import ru.practicum.shareit.request.dto.CreateItemRequest;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;

import java.util.Map;
import java.util.function.Supplier;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RestTemplateBuilder builder;

    @Test
    void testBaseClient() {
        TestableBaseClient client = new TestableBaseClient(restTemplate);

        ResponseEntity<Object> mockResponse = ResponseEntity.ok().build();

        lenient().when(restTemplate.exchange(
                anyString(),
                any(),
                any(),
                eq(Object.class)
        )).thenReturn(mockResponse);

        lenient().when(restTemplate.exchange(
                anyString(),
                any(),
                any(),
                eq(Object.class),
                any(Map.class)
        )).thenReturn(mockResponse);

        client.get("/test");
        client.get("/test", 1L);
        client.get("/test", 1L, Map.of("param", "value"));
        client.post("/test", "body");
        client.post("/test", 1L, "body");
        client.put("/test", 1L, "body");
        client.patch("/test", "body");
        client.patch("/test", 1L);
        client.patch("/test", 1L, "body");
        client.delete("/test");
        client.delete("/test", 1L);
    }

    @Test
    void testUserClient() {
        when(builder.uriTemplateHandler(any())).thenReturn(builder);
        when(builder.requestFactory(any(Supplier.class))).thenReturn(builder);
        when(builder.build()).thenReturn(restTemplate);

        lenient().when(restTemplate.exchange(anyString(), any(), any(), eq(Object.class)))
                .thenReturn(ResponseEntity.ok().build());

        UserClient client = new UserClient("http://test:8080", builder);
        client.createUser(new CreateUserRequest("U", "u@t.com"));
        client.updateUser(1L, new UpdateUserRequest("U2", "u2@t.com"));
        client.getUser(2L);
        client.deleteUser(3L);
    }

    @Test
    void testItemClient() {
        when(builder.uriTemplateHandler(any())).thenReturn(builder);
        when(builder.requestFactory(any(Supplier.class))).thenReturn(builder);
        when(builder.build()).thenReturn(restTemplate);

        lenient().when(restTemplate.exchange(anyString(), any(), any(), eq(Object.class)))
                .thenReturn(ResponseEntity.ok().build());
        lenient().when(restTemplate.exchange(anyString(), any(), any(), eq(Object.class), any(Map.class)))
                .thenReturn(ResponseEntity.ok().build());

        ItemClient client = new ItemClient("http://test:8080", builder);
        client.create(1L, new CreateItem("I", "D", true, 2L));
        client.update(1L, 2L, new UpdateItem("UI", "UD", true));
        client.getById(1L, 2L);
        client.getAll(1L);
        client.getByQuery(1L, "search");
        client.delete(1L, 2L);
        client.createComment(1L, 2L, new CreateComment("Good"));
    }

    @Test
    void testBookingClient() {
        when(builder.uriTemplateHandler(any())).thenReturn(builder);
        when(builder.requestFactory(any(Supplier.class))).thenReturn(builder);
        when(builder.build()).thenReturn(restTemplate);

        lenient().when(restTemplate.exchange(anyString(), any(), any(), eq(Object.class)))
                .thenReturn(ResponseEntity.ok().build());
        lenient().when(restTemplate.exchange(anyString(), any(), any(), eq(Object.class), any(Map.class)))
                .thenReturn(ResponseEntity.ok().build());

        BookingClient client = new BookingClient("http://test:8080", builder);

        client.getBookings(1L, BookingState.ALL, 0, 10);
        client.getBookingsByOwner(1L, BookingState.CURRENT, 0, 5);
        client.bookItem(1L, new CreateBookingRequest(1L,
                java.time.LocalDateTime.now().plusDays(1),
                java.time.LocalDateTime.now().plusDays(2)));
        client.getBooking(1L, 2L);
        client.updateBooking(1L, 2L, true);
        client.deleteBooking(1L, 2L);
    }

    @Test
    void testRequestClient() {
        when(builder.uriTemplateHandler(any())).thenReturn(builder);
        when(builder.requestFactory(any(Supplier.class))).thenReturn(builder);
        when(builder.build()).thenReturn(restTemplate);

        lenient().when(restTemplate.exchange(anyString(), any(), any(), eq(Object.class)))
                .thenReturn(ResponseEntity.ok().build());
        lenient().when(restTemplate.exchange(anyString(), any(), any(), eq(Object.class), any(Map.class)))
                .thenReturn(ResponseEntity.ok().build());

        RequestClient client = new RequestClient("http://test:8080", builder);

        client.create(1L, new CreateItemRequest("Need a hammer"));
        client.getByUser(1L);
        client.getAll();
        client.getById(1L);
    }
}