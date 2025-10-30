package ru.practicum.shareit.users;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static ru.practicum.shareit.users.UsersTestConfig.*;

@SpringBootTest(classes = UsersTestConfig.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class UserServiceTests {
    @Autowired
    private UserServiceImpl userService;

    @Autowired
    @Qualifier("firstTestUser")
    private User firstTestUser;

    @Autowired
    @Qualifier("secondTestUserNoId")
    private User secondTestUser;

    @Nested
    @DisplayName("Find User Operations")
    class FindUserTests {
        @Test
        @DisplayName("Should find existing user by ID")
        void findUserById() {
            UserDto resultUser = userService.findById(TEST_USER_1_ID);

            assertThat(resultUser)
                    .usingRecursiveComparison()
                    .isEqualTo(firstTestUser);
        }

        @Test
        @DisplayName("Should throw not found exception when searching non-existent user")
        void findUserByEmail() {
            long nonExistentId = 999L;

            assertThatThrownBy(() -> userService.findById(nonExistentId))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageMatching("User with ID %d not found".formatted(nonExistentId));
        }
    }

    @Nested
    @DisplayName("Create User Operations")
    class CreateUserTests {
        @Test
        @DisplayName("Should create new user with valid data")
        void createNewUser() {
            userService.create(secondTestUser);
            UserDto resultUser = userService.findById(TEST_USER_2_ID);

            assertThat(resultUser)
                    .usingRecursiveComparison()
                    .isEqualTo(secondTestUser);
        }

        @Test
        @DisplayName("Should throw duplicate exception when email already exists")
        void createUserWithExistingEmail() {
            User duplicateEmailUser = User.builder()
                    .name("Different Name")
                    .email(TEST_USER_1_EMAIL)
                    .build();

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
        void updateUserWithAllFields() {
            userService.update(secondTestUser, TEST_USER_1_ID);
            secondTestUser.setId(TEST_USER_1_ID);
            UserDto resultUser = userService.findById(TEST_USER_1_ID);

            assertThat(resultUser)
                    .usingRecursiveComparison()
                    .isEqualTo(secondTestUser);
        }

        @Test
        @DisplayName("Should update only name when email is null")
        void updateUserNameWithNullEmail() {
            secondTestUser.setEmail(null);
            userService.update(secondTestUser, TEST_USER_1_ID);
            secondTestUser.setId(TEST_USER_1_ID);
            secondTestUser.setEmail(TEST_USER_1_EMAIL);
            UserDto resultUser = userService.findById(TEST_USER_1_ID);

            assertThat(resultUser)
                    .usingRecursiveComparison()
                    .isEqualTo(secondTestUser);
        }

        @Test
        @DisplayName("Should update only email when name is null")
        void updateUserNameWithNullName() {
            secondTestUser.setName(null);
            userService.update(secondTestUser, TEST_USER_1_ID);
            secondTestUser.setId(TEST_USER_1_ID);
            secondTestUser.setName(TEST_USER_1_NAME);
            UserDto resultUser = userService.findById(TEST_USER_1_ID);

            assertThat(resultUser)
                    .usingRecursiveComparison()
                    .isEqualTo(secondTestUser);
        }

        @Test
        @DisplayName("Should throw duplicate exception when updating to existing email")
        void updateUserEmailToExisting() {
            userService.create(secondTestUser);

            assertThatThrownBy(() -> userService.update(firstTestUser, TEST_USER_2_ID))
                    .isInstanceOf(DuplicateException.class)
                    .hasMessageMatching("Email %s already exists".formatted(TEST_USER_1_EMAIL));        }
    }

    @Nested
    @DisplayName("Delete User Operations")
    class DeleteUserTests {
        @Test
        @DisplayName("Should delete existing user by ID")
        void deleteById() {
            userService.delete(TEST_USER_1_ID);

            assertThatThrownBy(() -> userService.findById(TEST_USER_1_ID))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageMatching("User with ID %d not found".formatted(TEST_USER_1_ID));
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent user")
        void deleteNonExistingUser() {
            long nonExistentId = 777L;

            assertThatThrownBy(() -> userService.delete(nonExistentId))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageMatching("User with ID %d not found".formatted(nonExistentId));
        }
    }
}