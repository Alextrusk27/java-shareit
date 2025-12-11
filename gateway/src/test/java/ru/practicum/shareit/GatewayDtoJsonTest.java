package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.item.dto.CreateComment;
import ru.practicum.shareit.item.dto.CreateItem;
import ru.practicum.shareit.item.dto.UpdateItem;
import ru.practicum.shareit.request.dto.CreateItemRequest;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@AutoConfigureJsonTesters
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DisplayName("JSON DTO Tests")
public class GatewayDtoJsonTest {

    @Nested
    @DisplayName("Users DTO")
    class UserDtoTests {

        @Nested
        @DisplayName("User Create DTO")
        @RequiredArgsConstructor(onConstructor_ = @Autowired)
        class CreateUserDtoTests {
            private final JacksonTester<CreateUserRequest> createUserJson;

            @Test
            @DisplayName("User -> serialize CreateUserRequest to JSON")
            void serializeCreateUserRequestToJson() throws IOException {
                CreateUserRequest request = new CreateUserRequest("John", "email@email.com");
                JsonContent<CreateUserRequest> result = createUserJson.write(request);

                assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("John");
                assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("email@email.com");
            }

            @Test
            @DisplayName("User -> deserialize JSON to CreateUserRequest")
            void deserializeJsonToCreateUserRequest() throws IOException {
                String json = """
                        {
                            "name": "John",
                            "email": "john@example.com"
                        }
                        """;
                CreateUserRequest request = createUserJson.parse(json).getObject();

                assertThat(request.name()).isEqualTo("John");
                assertThat(request.email()).isEqualTo("john@example.com");
            }
        }

        @Nested
        @DisplayName("User Update DTO")
        @RequiredArgsConstructor(onConstructor_ = @Autowired)
        class UpdateUserDtoTests {
            private final JacksonTester<UpdateUserRequest> updateUserJson;

            @Test
            @DisplayName("User -> serialize UpdateUserRequest to JSON")
            void serializeUpdateUserRequestToJson() throws IOException {
                UpdateUserRequest request = new UpdateUserRequest("John Updated", "updated@email.com");
                JsonContent<UpdateUserRequest> result = updateUserJson.write(request);

                assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("John Updated");
                assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("updated@email.com");
            }

            @Test
            @DisplayName("User -> deserialize JSON to UpdateUserRequest")
            void deserializeJsonToUpdateUserRequest() throws IOException {
                String json = """
                        {
                            "name": "John Updated",
                            "email": "john.updated@example.com"
                        }
                        """;
                UpdateUserRequest request = updateUserJson.parse(json).getObject();

                assertThat(request.name()).isEqualTo("John Updated");
                assertThat(request.email()).isEqualTo("john.updated@example.com");
            }
        }
    }

    @Nested
    @DisplayName("Items DTO")
    class ItemDtoTests {

        @Nested
        @DisplayName("Item Create DTO")
        @RequiredArgsConstructor(onConstructor_ = @Autowired)
        class CreateItemDtoTests {
            private final JacksonTester<CreateItem> createItemJson;

            @Test
            @DisplayName("Item -> serialize CreateItem to JSON")
            void serializeCreateItemToJson() throws IOException {
                CreateItem request = new CreateItem("Drill", "Powerful electric drill", true, 123L);
                JsonContent<CreateItem> result = createItemJson.write(request);

                assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Drill");
                assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Powerful electric drill");
                assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
                assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(123);
            }

            @Test
            @DisplayName("Item -> deserialize JSON to CreateItem")
            void deserializeJsonToCreateItem() throws IOException {
                String json = """
                        {
                            "name": "Hammer",
                            "description": "Heavy duty hammer",
                            "available": false,
                            "requestId": 456
                        }
                        """;
                CreateItem request = createItemJson.parse(json).getObject();

                assertThat(request.name()).isEqualTo("Hammer");
                assertThat(request.description()).isEqualTo("Heavy duty hammer");
                assertThat(request.available()).isFalse();
                assertThat(request.requestId()).isEqualTo(456L);
            }
        }

        @Nested
        @DisplayName("Item Update DTO")
        @RequiredArgsConstructor(onConstructor_ = @Autowired)
        class UpdateItemDtoTests {
            private final JacksonTester<UpdateItem> updateItemJson;

            @Test
            @DisplayName("Item -> serialize UpdateItem to JSON")
            void serializeUpdateItemToJson() throws IOException {
                UpdateItem request = new UpdateItem("Updated Drill", "More powerful electric drill", false);
                JsonContent<UpdateItem> result = updateItemJson.write(request);

                assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Updated Drill");
                assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("More powerful electric drill");
                assertThat(result).extractingJsonPathBooleanValue("$.available").isFalse();
            }

            @Test
            @DisplayName("Item -> deserialize JSON to UpdateItem")
            void deserializeJsonToUpdateItem() throws IOException {
                String json = """
                {
                    "name": "Updated Hammer",
                    "description": "Lighter and more efficient",
                    "available": true
                }
                """;
                UpdateItem request = updateItemJson.parse(json).getObject();

                assertThat(request.name()).isEqualTo("Updated Hammer");
                assertThat(request.description()).isEqualTo("Lighter and more efficient");
                assertThat(request.available()).isTrue();
            }
        }

        @Nested
        @DisplayName("Comment Create DTO")
        @RequiredArgsConstructor(onConstructor_ = @Autowired)
        class CreateCommentDtoTests {
            private final JacksonTester<CreateComment> createCommentJson;

            @Test
            @DisplayName("Comment -> serialize CreateComment to JSON")
            void serializeCreateCommentToJson() throws IOException {
                CreateComment request = new CreateComment("Great item, works perfectly!");
                JsonContent<CreateComment> result = createCommentJson.write(request);

                assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Great item, works perfectly!");
            }

            @Test
            @DisplayName("Comment -> deserialize JSON to CreateComment")
            void deserializeJsonToCreateComment() throws IOException {
                String json = """
                {
                    "text": "Very useful tool, thank you!"
                }
                """;
                CreateComment request = createCommentJson.parse(json).getObject();

                assertThat(request.text()).isEqualTo("Very useful tool, thank you!");
            }
        }
    }

    @Nested
    @DisplayName("Bookings DTO")
    class BookingDtoTests {

        @Nested
        @DisplayName("Booking Create DTO")
        @RequiredArgsConstructor(onConstructor_ = @Autowired)
        class CreateBookingDtoTests {
            private final JacksonTester<CreateBookingRequest> createBookingJson;

            @Test
            @DisplayName("Booking -> serialize CreateBookingRequest to JSON")
            void serializeCreateBookingRequestToJson() throws IOException {
                LocalDateTime start = LocalDateTime.now().plusDays(1);
                LocalDateTime end = LocalDateTime.now().plusDays(2);

                CreateBookingRequest request = new CreateBookingRequest(123L, start, end);
                JsonContent<CreateBookingRequest> result = createBookingJson.write(request);

                assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(123);
                assertThat(result).extractingJsonPathStringValue("$.start").isNotNull();
                assertThat(result).extractingJsonPathStringValue("$.end").isNotNull();
            }

            @Test
            @DisplayName("Booking -> deserialize JSON to CreateBookingRequest")
            void deserializeJsonToCreateBookingRequest() throws IOException {
                String json = """
                {
                    "itemId": 456,
                    "start": "2024-01-15T10:00:00",
                    "end": "2024-01-20T10:00:00"
                }
                """;
                CreateBookingRequest request = createBookingJson.parse(json).getObject();

                assertThat(request.itemId()).isEqualTo(456L);
                assertThat(request.start()).isEqualTo(LocalDateTime.parse("2024-01-15T10:00:00"));
                assertThat(request.end()).isEqualTo(LocalDateTime.parse("2024-01-20T10:00:00"));
            }
        }
    }

    @Nested
    @DisplayName("Item Requests DTO")
    class ItemRequestDtoTests {

        @Nested
        @DisplayName("Item Request Create DTO")
        @RequiredArgsConstructor(onConstructor_ = @Autowired)
        class CreateItemRequestDtoTests {
            private final JacksonTester<CreateItemRequest> createItemRequestJson;

            @Test
            @DisplayName("Item Request -> serialize CreateItemRequest to JSON")
            void serializeCreateItemRequestToJson() throws IOException {
                CreateItemRequest request = new CreateItemRequest("Need a powerful drill for home renovation");
                JsonContent<CreateItemRequest> result = createItemRequestJson.write(request);

                assertThat(result).extractingJsonPathStringValue("$.description")
                        .isEqualTo("Need a powerful drill for home renovation");
            }

            @Test
            @DisplayName("Item Request -> deserialize JSON to CreateItemRequest")
            void deserializeJsonToCreateItemRequest() throws IOException {
                String json = """
                {
                    "description": "Looking for a camping tent for 4 people"
                }
                """;
                CreateItemRequest request = createItemRequestJson.parse(json).getObject();

                assertThat(request.description()).isEqualTo("Looking for a camping tent for 4 people");
            }
        }
    }
}
