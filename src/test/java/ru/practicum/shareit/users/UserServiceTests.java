package ru.practicum.shareit.users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.exceptions.DuplicateException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.*;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static ru.practicum.shareit.users.UsersTestConfig.*;

@SpringBootTest(classes = UsersTestConfig.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@DisplayName("UserService CRUD operations")
public class UserServiceTests {
    @Autowired
    private UserServiceImplTest userService;

    @BeforeEach
    public void setup() {
        userService.resetTestData();
    }

    @Nested
    @DisplayName("Find User Operations")
    class FindUserTests {
        @Test
        @DisplayName("Should find existing user by ID")
        void findById_WhenUserExists_ReturnsUser() {
            UserDto result = userService.findById(TEST_USER_1_ID);

            assertThat(result)
                    .extracting(UserDto::name, UserDto::email)
                    .containsExactly(TEST_USER_1_NAME, TEST_USER_1_EMAIL);
        }

        @Test
        @DisplayName("Should throw NotFoundException when searching non-existent user")
        void findById_WhenUserNotExists_ThrowsNotFoundException() {
            long nonExistentId = 999L;

            assertThatThrownBy(() -> userService.findById(nonExistentId))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageMatching("User id=%d not found".formatted(nonExistentId));
        }
    }

    @Nested
    @DisplayName("Create User Operations")
    class CreateUserTests {
        @Test
        @DisplayName("Should create new user with valid data")
        void create_WithValidData_CreatesUser() {
            CreateUserRequest createSecondTestUser = new CreateUserRequest(TEST_USER_2_NAME, TEST_USER_2_EMAIL);
            userService.create(createSecondTestUser);
            UserDto result = userService.findById(TEST_USER_2_ID);

            assertThat(result)
                    .extracting(UserDto::name, UserDto::email)
                    .containsExactly(TEST_USER_2_NAME, TEST_USER_2_EMAIL);
        }

        @Test
        @DisplayName("Should throw DuplicateException when email already exists")
        void create_WithExistingEmail_ThrowsDuplicateException() {
            CreateUserRequest duplicateEmailUser = new CreateUserRequest("Different Name", TEST_USER_1_EMAIL);

            assertThatThrownBy(() -> userService.create(duplicateEmailUser))
                    .isInstanceOf(DuplicateException.class)
                    .hasMessageMatching("Email %s already exists".formatted(TEST_USER_1_EMAIL));
        }
    }

    @Nested
    @DisplayName("Update User Operations")
    class UpdateUserTests {
        @Test
        @DisplayName("Should update all fields when all fields provided")
        void update_WhenAllFieldsProvided_UpdatesAllFields() {
            UpdateUserRequest updateUser = new UpdateUserRequest(TEST_USER_2_NAME, TEST_USER_2_EMAIL);
            userService.update(updateUser, TEST_USER_1_ID);
            UserDto result = userService.findById(TEST_USER_1_ID);

            assertThat(result)
                    .extracting(UserDto::name, UserDto::email)
                    .containsExactly(TEST_USER_2_NAME, TEST_USER_2_EMAIL);
        }

        @Test
        @DisplayName("Should update only name and preserve email when email is null")
        void update_WhenOnlyNameProvided_UpdatesNameAndPreservesEmail() {
            UpdateUserRequest updateRequest = new UpdateUserRequest(TEST_USER_2_NAME, null);
            userService.update(updateRequest, TEST_USER_1_ID);
            UserDto result = userService.findById(TEST_USER_1_ID);

            assertThat(result)
                    .extracting(UserDto::name, UserDto::email)
                    .containsExactly(TEST_USER_2_NAME, TEST_USER_1_EMAIL);
        }

        @Test
        @DisplayName("Should update only email and preserve name when name is null")
        void update_WhenOnlyEmailProvided_UpdatesEmailAndPreservesName() {
            UpdateUserRequest updateRequest = new UpdateUserRequest(null, TEST_USER_2_EMAIL);
            userService.update(updateRequest, TEST_USER_1_ID);
            UserDto result = userService.findById(TEST_USER_1_ID);

            assertThat(result)
                    .extracting(UserDto::name, UserDto::email)
                    .containsExactly(TEST_USER_1_NAME, TEST_USER_2_EMAIL);
        }

        @Test
        @DisplayName("Should throw DuplicateException when updating to existing email")
        void update_WithExistingEmail_ThrowsDuplicateException() {
            CreateUserRequest createRequest = new CreateUserRequest(TEST_USER_2_EMAIL, TEST_USER_2_EMAIL);
            userService.create(createRequest);
            UpdateUserRequest updateRequest = new UpdateUserRequest("Some_new_name", TEST_USER_1_EMAIL);

            assertThatThrownBy(() -> userService.update(updateRequest, TEST_USER_2_ID))
                    .isInstanceOf(DuplicateException.class)
                    .hasMessageMatching("Email %s already exists".formatted(TEST_USER_1_EMAIL));
        }
    }

    @Nested
    @DisplayName("Delete User Operations")
    class DeleteUserTests {
        @Test
        @DisplayName("Should delete existing user by ID")
        void delete_WhenUserExists_DeletesUser() {
            userService.delete(TEST_USER_1_ID);

            assertThatThrownBy(() -> userService.findById(TEST_USER_1_ID))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageMatching("User id=%d not found".formatted(TEST_USER_1_ID));
        }

        @Test
        @DisplayName("Should throw NotFoundException when deleting non-existent user")
        void delete_WhenUserNotExists_ThrowsNotFoundException() {
            long nonExistentId = 777L;

            assertThatThrownBy(() -> userService.delete(nonExistentId))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageMatching("User id=%d not found".formatted(nonExistentId));
        }

        @Test
        @DisplayName("Should delete user and all associated items")
        void delete_WhenUserHasItems_DeletesUserAndAllItems() {
            userService.delete(TEST_USER_1_ID);
            Map<Long, Item> items = userService.getTestItemRepository().getItems();

            assertThat(items.values())
                    .noneMatch(item -> item.getUserId().equals(TEST_USER_1_ID));
        }
    }
}