package ru.practicum.shareit;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.item.dto.CreateComment;
import ru.practicum.shareit.item.dto.CreateItem;
import ru.practicum.shareit.item.dto.UpdateItem;
import ru.practicum.shareit.request.dto.CreateItemRequest;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class GatewayValidationTest {

    private final Validator validator;

    public GatewayValidationTest() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Nested
    @DisplayName("User DTO validation")
    class UserDtoValidationTest {

        @Nested
        @DisplayName("CreateUserRequest validation")
        class CreateUserRequestValidationTest {

            @Test
            @DisplayName("Valid CreateUserRequest -> should have no violations")
            void createUserRequest_withValidData_shouldHaveNoViolations() {
                CreateUserRequest request = new CreateUserRequest("John Locke", "john@example.com");
                Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

                assertThat(violations).isEmpty();
            }

            @ParameterizedTest
            @NullAndEmptySource
            @ValueSource(strings = {" ", "  ", "\t", "\n"})
            @DisplayName("CreateUserRequest with blank name -> should have violation")
            void createUserRequest_withBlankName_shouldHaveViolation(String name) {
                CreateUserRequest request = new CreateUserRequest(name, "john@example.com");
                Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

                assertThat(violations).hasSize(1);
                assertThat(violations.iterator().next().getMessage())
                        .isEqualTo("User name cannot be empty");
            }

            @Test
            @DisplayName("CreateUserRequest with null name -> should have violation")
            void createUserRequest_withNullName_shouldHaveViolation() {
                CreateUserRequest request = new CreateUserRequest(null, "john@example.com");
                Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

                assertThat(violations).hasSize(1);
                assertThat(violations.iterator().next().getMessage())
                        .isEqualTo("User name cannot be empty");
            }

            @Test
            @DisplayName("CreateUserRequest with too long name -> should have violation")
            void createUserRequest_withTooLongName_shouldHaveViolation() {
                String longName = "A".repeat(31);
                CreateUserRequest request = new CreateUserRequest(longName, "john@example.com");
                Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

                assertThat(violations).hasSize(1);
                assertThat(violations.iterator().next().getMessage())
                        .isEqualTo("Name must be no longer than 30 characters");
            }

            @Test
            @DisplayName("CreateUserRequest with name at max length -> should have no violations")
            void createUserRequest_withNameAtMaxLength_shouldHaveNoViolations() {
                String maxLengthName = "A".repeat(30);
                CreateUserRequest request = new CreateUserRequest(maxLengthName, "john@example.com");
                Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

                assertThat(violations).isEmpty();
            }

            @ParameterizedTest
            @ValueSource(strings = {
                    "John@Locke",
                    "Name#123",
                    "User$Name",
                    "Test&Name",
                    "Name*Star",
                    "Invalid[Name]",
                    "Bad{Name}"
            })
            @DisplayName("CreateUserRequest with invalid characters in name -> should have violation")
            void createUserRequest_withInvalidCharactersInName_shouldHaveViolation(String name) {
                CreateUserRequest request = new CreateUserRequest(name, "john@example.com");
                Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

                assertThat(violations).hasSize(1);
                assertThat(violations.iterator().next().getMessage())
                        .isEqualTo("User name contains invalid characters");
            }

            @ParameterizedTest
            @ValueSource(strings = {
                    "John Locke",
                    "John-Locke",
                    "John.Locke",
                    "John,Locke",
                    "John!Locke",
                    "John?Locke",
                    "John(Locke)",
                    "John O'Connor",
                    "John123",
                    "Jöhn Müller",
                    "Иван Иванов"
            })
            @DisplayName("CreateUserRequest with valid special characters in name -> should have no violations")
            void createUserRequest_withValidSpecialCharactersInName_shouldHaveNoViolations(String name) {
                CreateUserRequest request = new CreateUserRequest(name, "john@example.com");
                Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

                assertThat(violations).isEmpty();
            }

            @ParameterizedTest
            @NullAndEmptySource
            @ValueSource(strings = {" ", "  ", "\t", "\n"})
            @DisplayName("CreateUserRequest with blank email -> should have @NotBlank violation")
            void createUserRequest_withBlankEmail_shouldHaveNotBlankViolation(String email) {
                CreateUserRequest request = new CreateUserRequest("John Locke", email);
                Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

                assertThat(violations).isNotEmpty();
                assertThat(violations)
                        .extracting(ConstraintViolation::getMessage)
                        .contains("Email cannot be empty");
            }

            @Test
            @DisplayName("CreateUserRequest with null email -> should have violation")
            void createUserRequest_withNullEmail_shouldHaveViolation() {
                CreateUserRequest request = new CreateUserRequest("John Locke", null);
                Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

                assertThat(violations).hasSize(1);
                assertThat(violations.iterator().next().getMessage())
                        .isEqualTo("Email cannot be empty");
            }

            @ParameterizedTest
            @ValueSource(strings = {
                    "not-an-email",
                    "john@",
                    "@example.com",
                    "john@.com",
                    "john@example.",
                    "john example.com",
                    "john@example@com"
            })
            @DisplayName("CreateUserRequest with invalid email format -> should have violation")
            void createUserRequest_withInvalidEmailFormat_shouldHaveViolation(String email) {
                CreateUserRequest request = new CreateUserRequest("John Locke", email);
                Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

                assertThat(violations).hasSize(1);
                assertThat(violations.iterator().next().getMessage())
                        .isEqualTo("Invalid email format");
            }

            @ParameterizedTest
            @ValueSource(strings = {
                    "john@example.com",
                    "jane.Locke@company.co.uk",
                    "user123@subdomain.example.org",
                    "test+tag@example.com",
                    "user_name@example.io",
                    "info@example.ru",
                    "contact@example.technology"
            })
            @DisplayName("CreateUserRequest with valid email formats -> should have no violations")
            void createUserRequest_withValidEmailFormats_shouldHaveNoViolations(String email) {
                CreateUserRequest request = new CreateUserRequest("John Locke", email);
                Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

                assertThat(violations).isEmpty();
            }

            @Test
            @DisplayName("CreateUserRequest with multiple violations -> should have all violations")
            void createUserRequest_withMultipleViolations_shouldHaveAllViolations() {
                CreateUserRequest request = new CreateUserRequest("", "invalid-email");
                Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

                assertThat(violations).hasSize(2);
                assertThat(violations)
                        .extracting(ConstraintViolation::getMessage)
                        .containsExactlyInAnyOrder(
                                "User name cannot be empty",
                                "Invalid email format"
                        );
            }

            @Test
            @DisplayName("CreateUserRequest with all invalid fields -> should have all violations")
            void createUserRequest_withAllInvalidFields_shouldHaveAllViolations() {
                CreateUserRequest request = new CreateUserRequest("", null);
                Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

                assertThat(violations).hasSize(2);
            }

            @Test
            @DisplayName("CreateUserRequest with valid Unicode name -> should have no violations")
            void createUserRequest_withValidUnicodeName_shouldHaveNoViolations() {
                CreateUserRequest request = new CreateUserRequest("Mária González-Ñúñez", "maria@example.com");
                Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

                assertThat(violations).isEmpty();
            }

            @Test
            @DisplayName("CreateUserRequest with name containing apostrophe -> should have no violations")
            void createUserRequest_withNameContainingApostrophe_shouldHaveNoViolations() {
                CreateUserRequest request = new CreateUserRequest("O'Connor", "oconnor@example.com");
                Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

                assertThat(violations).isEmpty();
            }

            @Test
            @DisplayName("CreateUserRequest with name containing numbers -> should have no violations")
            void createUserRequest_withNameContainingNumbers_shouldHaveNoViolations() {
                CreateUserRequest request = new CreateUserRequest("John123 Locke456", "john@example.com");
                Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

                assertThat(violations).isEmpty();
            }
        }

        @Nested
        @DisplayName("UpdateUserRequest validation")
        class UpdateUserRequestValidationTest {

            @Test
            @DisplayName("Valid UpdateUserRequest -> should have no violations")
            void updateUserRequest_withValidData_shouldHaveNoViolations() {
                UpdateUserRequest request = new UpdateUserRequest("John Updated", "updated@example.com");
                Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

                assertThat(violations).isEmpty();
            }

            @Test
            @DisplayName("UpdateUserRequest with null values -> should have no violations (all fields optional)")
            void updateUserRequest_withNullValues_shouldHaveNoViolations() {
                UpdateUserRequest request = new UpdateUserRequest(null, null);
                Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

                assertThat(violations).isEmpty();
            }

            @Test
            @DisplayName("UpdateUserRequest with null name and valid email -> should have no violations")
            void updateUserRequest_withNullNameValidEmail_shouldHaveNoViolations() {
                UpdateUserRequest request = new UpdateUserRequest(null, "email@example.com");
                Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

                assertThat(violations).isEmpty();
            }

            @Test
            @DisplayName("UpdateUserRequest with valid name and null email -> should have no violations")
            void updateUserRequest_withValidNameNullEmail_shouldHaveNoViolations() {
                UpdateUserRequest request = new UpdateUserRequest("John", null);
                Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

                assertThat(violations).isEmpty();
            }

            @Test
            @DisplayName("UpdateUserRequest with empty name -> should have no violations (not @NotBlank)")
            void updateUserRequest_withEmptyName_shouldHaveNoViolations() {
                UpdateUserRequest request = new UpdateUserRequest("", "email@example.com");
                Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

                assertThat(violations).isEmpty();
            }

            @Test
            @DisplayName("UpdateUserRequest with empty email -> should have no violations (not @NotBlank)")
            void updateUserRequest_withEmptyEmail_shouldHaveNoViolations() {
                UpdateUserRequest request = new UpdateUserRequest("John", "");
                Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

                assertThat(violations).isEmpty();
            }

            @Test
            @DisplayName("UpdateUserRequest with too long name -> should have violation")
            void updateUserRequest_withTooLongName_shouldHaveViolation() {
                String longName = "A".repeat(31);
                UpdateUserRequest request = new UpdateUserRequest(longName, "email@example.com");
                Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

                assertThat(violations).hasSize(1);
                assertThat(violations.iterator().next().getMessage())
                        .isEqualTo("Name must be no longer than 30 characters");
            }

            @Test
            @DisplayName("UpdateUserRequest with name at max length -> should have no violations")
            void updateUserRequest_withNameAtMaxLength_shouldHaveNoViolations() {
                String maxLengthName = "A".repeat(30);
                UpdateUserRequest request = new UpdateUserRequest(maxLengthName, "email@example.com");
                Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

                assertThat(violations).isEmpty();
            }

            @ParameterizedTest
            @ValueSource(strings = {
                    "John@Doe",
                    "Name#123",
                    "User$Name",
                    "Test&Name",
                    "Name*Star",
                    "Invalid[Name]",
                    "Bad{Name}",
                    "Name|Pipe"
            })
            @DisplayName("UpdateUserRequest with invalid characters in name -> should have violation")
            void updateUserRequest_withInvalidCharactersInName_shouldHaveViolation(String name) {
                UpdateUserRequest request = new UpdateUserRequest(name, "email@example.com");
                Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

                assertThat(violations).hasSize(1);
                assertThat(violations.iterator().next().getMessage())
                        .isEqualTo("User name contains invalid characters");
            }

            @ParameterizedTest
            @ValueSource(strings = {
                    "John Doe",
                    "John-Doe",
                    "John.Doe",
                    "John,Doe",
                    "John!Doe",
                    "John?Doe",
                    "John(Doe)",
                    "John O'Connor",
                    "John123",
                    "Jöhn Müller",
                    "Иван Иванов",
            })
            @DisplayName("UpdateUserRequest with valid special characters in name -> should have no violations")
            void updateUserRequest_withValidSpecialCharactersInName_shouldHaveNoViolations(String name) {
                UpdateUserRequest request = new UpdateUserRequest(name, "email@example.com");
                Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

                assertThat(violations).isEmpty();
            }

            @ParameterizedTest
            @ValueSource(strings = {
                    "not-an-email",
                    "john@",
                    "@example.com",
                    "john@.com",
                    "john@example.",
                    "john example.com",
                    "john@example@com"
            })
            @DisplayName("UpdateUserRequest with invalid email format -> should have violation")
            void updateUserRequest_withInvalidEmailFormat_shouldHaveViolation(String email) {
                UpdateUserRequest request = new UpdateUserRequest("John", email);
                Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

                assertThat(violations).hasSize(1);
                assertThat(violations.iterator().next().getMessage())
                        .isEqualTo("Invalid email format");
            }

            @Test
            @DisplayName("UpdateUserRequest with empty string email -> should have no violations (empty string passes @Email)")
            void updateUserRequest_withEmptyStringEmail_shouldHaveNoViolations() {
                UpdateUserRequest request = new UpdateUserRequest("John", "");
                Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

                assertThat(violations).isEmpty();
            }

            @ParameterizedTest
            @ValueSource(strings = {
                    "john@example.com",
                    "jane.doe@company.co.uk",
                    "user123@subdomain.example.org",
                    "test+tag@example.com",
                    "user_name@example.io",
                    "info@example.ru",
                    "contact@example.technology"
            })
            @DisplayName("UpdateUserRequest with valid email formats -> should have no violations")
            void updateUserRequest_withValidEmailFormats_shouldHaveNoViolations(String email) {
                UpdateUserRequest request = new UpdateUserRequest("John", email);
                Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

                assertThat(violations).isEmpty();
            }

            @Test
            @DisplayName("UpdateUserRequest with whitespace email -> should have violation")
            void updateUserRequest_withWhitespaceEmail_shouldHaveViolation() {
                UpdateUserRequest request = new UpdateUserRequest("John", "   ");
                Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

                assertThat(violations).hasSize(1);
                assertThat(violations.iterator().next().getMessage())
                        .isEqualTo("Invalid email format");
            }

            @Test
            @DisplayName("UpdateUserRequest with multiple violations -> should have all violations")
            void updateUserRequest_withMultipleViolations_shouldHaveAllViolations() {
                String longName = "A".repeat(31);
                UpdateUserRequest request = new UpdateUserRequest(longName, "invalid-email");
                Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

                assertThat(violations).hasSize(2);
                assertThat(violations)
                        .extracting(ConstraintViolation::getMessage)
                        .containsExactlyInAnyOrder(
                                "Name must be no longer than 30 characters",
                                "Invalid email format"
                        );
            }

            @Test
            @DisplayName("UpdateUserRequest with invalid name and valid email -> should have only name violation")
            void updateUserRequest_withInvalidNameValidEmail_shouldHaveOnlyNameViolation() {
                UpdateUserRequest request = new UpdateUserRequest("Name@Invalid", "valid@example.com");
                Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

                assertThat(violations).hasSize(1);
                assertThat(violations.iterator().next().getMessage())
                        .isEqualTo("User name contains invalid characters");
            }

            @Test
            @DisplayName("UpdateUserRequest with valid name and invalid email -> should have only email violation")
            void updateUserRequest_withValidNameInvalidEmail_shouldHaveOnlyEmailViolation() {
                UpdateUserRequest request = new UpdateUserRequest("Valid Name", "not-an-email");
                Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

                assertThat(violations).hasSize(1);
                assertThat(violations.iterator().next().getMessage())
                        .isEqualTo("Invalid email format");
            }

            @Test
            @DisplayName("UpdateUserRequest with only name update (null email) -> should have no violations")
            void updateUserRequest_withOnlyNameUpdate_shouldHaveNoViolations() {
                UpdateUserRequest request = new UpdateUserRequest("Updated Name", null);
                Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

                assertThat(violations).isEmpty();
            }

            @Test
            @DisplayName("UpdateUserRequest with only email update (null name) -> should have no violations")
            void updateUserRequest_withOnlyEmailUpdate_shouldHaveNoViolations() {
                UpdateUserRequest request = new UpdateUserRequest(null, "updated@example.com");
                Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

                assertThat(violations).isEmpty();
            }

            @Test
            @DisplayName("UpdateUserRequest with both fields updated -> should have no violations if valid")
            void updateUserRequest_withBothFieldsUpdated_shouldHaveNoViolations() {
                UpdateUserRequest request = new UpdateUserRequest("New Name", "new.email@example.com");
                Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

                assertThat(violations).isEmpty();
            }

            @Test
            @DisplayName("UpdateUserRequest with name containing apostrophe -> should have no violations")
            void updateUserRequest_withNameContainingApostrophe_shouldHaveNoViolations() {
                UpdateUserRequest request = new UpdateUserRequest("O'Connor", "email@example.com");
                Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

                assertThat(violations).isEmpty();
            }

            @Test
            @DisplayName("UpdateUserRequest with name containing exclamation -> should have no violations")
            void updateUserRequest_withNameContainingExclamation_shouldHaveNoViolations() {
                UpdateUserRequest request = new UpdateUserRequest("Wow!", "email@example.com");
                Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

                assertThat(violations).isEmpty();
            }

            @Test
            @DisplayName("UpdateUserRequest with name containing parentheses -> should have no violations")
            void updateUserRequest_withNameContainingParentheses_shouldHaveNoViolations() {
                UpdateUserRequest request = new UpdateUserRequest("John (The Boss)", "email@example.com");
                Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

                assertThat(violations).isEmpty();
            }
        }
    }

    @Nested
    @DisplayName("User DTO validation")
    class ItemDtoValidationTest {

        @Nested
        @DisplayName("CreateItem validation")
        class CreateItemValidationTest {

            @Test
            @DisplayName("Valid CreateItem -> should have no violations")
            void createItem_withValidData_shouldHaveNoViolations() {
                CreateItem request = new CreateItem("Drill", "Powerful electric drill", true, 123L);
                Set<ConstraintViolation<CreateItem>> violations = validator.validate(request);

                assertThat(violations).isEmpty();
            }

            @Test
            @DisplayName("CreateItem with null name -> should have @NotBlank violation")
            void createItem_withNullName_shouldHaveNotBlankViolation() {
                CreateItem request = new CreateItem(null, "Description", true, 123L);
                Set<ConstraintViolation<CreateItem>> violations = validator.validate(request);

                assertThat(violations).hasSize(1);
                assertThat(violations.iterator().next().getMessage())
                        .isEqualTo("Item name cannot be empty");
            }

            @Test
            @DisplayName("CreateItem with invalid characters in name -> should have @Pattern violation")
            void createItem_withInvalidCharactersInName_shouldHavePatternViolation() {
                CreateItem request = new CreateItem("Drill@", "Description", true, 123L);
                Set<ConstraintViolation<CreateItem>> violations = validator.validate(request);

                assertThat(violations).hasSize(1);
                assertThat(violations.iterator().next().getMessage())
                        .isEqualTo("Item name contains invalid characters");
            }

            @Test
            @DisplayName("CreateItem with too long name -> should have @Size violation")
            void createItem_withTooLongName_shouldHaveSizeViolation() {
                String longName = "A".repeat(51);
                CreateItem request = new CreateItem(longName, "Description", true, 123L);
                Set<ConstraintViolation<CreateItem>> violations = validator.validate(request);

                assertThat(violations).hasSize(1);
                assertThat(violations.iterator().next().getMessage())
                        .isEqualTo("Name must be no longer than 50 characters");
            }

            @Test
            @DisplayName("CreateItem with null description -> should have @NotBlank violation")
            void createItem_withNullDescription_shouldHaveNotBlankViolation() {
                CreateItem request = new CreateItem("Drill", null, true, 123L);
                Set<ConstraintViolation<CreateItem>> violations = validator.validate(request);

                assertThat(violations).hasSize(1);
                assertThat(violations.iterator().next().getMessage())
                        .isEqualTo("Item description cannot be empty");
            }

            @Test
            @DisplayName("CreateItem with invalid characters in description -> should have @Pattern violation")
            void createItem_withInvalidCharactersInDescription_shouldHavePatternViolation() {
                CreateItem request = new CreateItem("Drill", "Description!", true, 123L);
                Set<ConstraintViolation<CreateItem>> violations = validator.validate(request);

                assertThat(violations).hasSize(1);
                assertThat(violations.iterator().next().getMessage())
                        .isEqualTo("Item description contains invalid characters");
            }

            @Test
            @DisplayName("CreateItem with too long description -> should have @Size violation")
            void createItem_withTooLongDescription_shouldHaveSizeViolation() {
                String longDescription = "A".repeat(301);
                CreateItem request = new CreateItem("Drill", longDescription, true, 123L);
                Set<ConstraintViolation<CreateItem>> violations = validator.validate(request);

                assertThat(violations).hasSize(1);
                assertThat(violations.iterator().next().getMessage())
                        .isEqualTo("Description must be no longer than 300 characters");
            }

            @Test
            @DisplayName("CreateItem with null available -> should have @NotNull violation")
            void createItem_withNullAvailable_shouldHaveNotNullViolation() {
                CreateItem request = new CreateItem("Drill", "Description", null, 123L);
                Set<ConstraintViolation<CreateItem>> violations = validator.validate(request);

                assertThat(violations).hasSize(1);
                assertThat(violations.iterator().next().getMessage())
                        .isEqualTo("Available status is required");
            }

            @Test
            @DisplayName("CreateItem with zero requestId -> should have @Positive violation")
            void createItem_withZeroRequestId_shouldHavePositiveViolation() {
                CreateItem request = new CreateItem("Drill", "Description", true, 0L);
                Set<ConstraintViolation<CreateItem>> violations = validator.validate(request);

                assertThat(violations).hasSize(1);
                assertThat(violations.iterator().next().getMessage())
                        .isEqualTo("Request ID must be greater than 0");
            }

            @Test
            @DisplayName("CreateItem with null requestId -> should have no violations (optional field)")
            void createItem_withNullRequestId_shouldHaveNoViolations() {
                CreateItem request = new CreateItem("Drill", "Description", true, null);
                Set<ConstraintViolation<CreateItem>> violations = validator.validate(request);

                assertThat(violations).isEmpty();
            }

            @Test
            @DisplayName("CreateItem with negative requestId -> should have @Positive violation")
            void createItem_withNegativeRequestId_shouldHavePositiveViolation() {
                CreateItem request = new CreateItem("Drill", "Description", true, -1L);
                Set<ConstraintViolation<CreateItem>> violations = validator.validate(request);

                assertThat(violations).hasSize(1);
                assertThat(violations.iterator().next().getMessage())
                        .isEqualTo("Request ID must be greater than 0");
            }

            @Test
            @DisplayName("CreateItem with multiple violations -> should have all violations")
            void createItem_withMultipleViolations_shouldHaveAllViolations() {
                CreateItem request = new CreateItem("", "", null, 0L);
                Set<ConstraintViolation<CreateItem>> violations = validator.validate(request);

                assertThat(violations).hasSize(6);
                assertThat(violations)
                        .extracting(ConstraintViolation::getMessage)
                        .containsExactlyInAnyOrder(
                                "Item name cannot be empty",
                                "Item name contains invalid characters",
                                "Item description cannot be empty",
                                "Item description contains invalid characters",
                                "Available status is required",
                                "Request ID must be greater than 0"
                        );
            }
        }

        @Nested
        @DisplayName("UpdateItem validation")
        class UpdateItemValidationTest {

            @Test
            @DisplayName("Valid UpdateItem -> should have no violations")
            void updateItem_withValidData_shouldHaveNoViolations() {
                UpdateItem request = new UpdateItem("Updated Drill", "More powerful drill", true);
                Set<ConstraintViolation<UpdateItem>> violations = validator.validate(request);

                assertThat(violations).isEmpty();
            }

            @Test
            @DisplayName("UpdateItem with @Pattern violation in name -> should detect invalid characters")
            void updateItem_shouldHavePatternViolationForInvalidNameCharacters() {
                UpdateItem request = new UpdateItem("Drill@Invalid", "Valid description", true);
                Set<ConstraintViolation<UpdateItem>> violations = validator.validate(request);

                assertThat(violations).hasSize(1);
                assertThat(violations.iterator().next().getMessage())
                        .isEqualTo("Item title contains invalid characters");
            }

            @Test
            @DisplayName("UpdateItem with @Size violation in name -> should detect too long name")
            void updateItem_shouldHaveSizeViolationForTooLongName() {
                String longName = "A".repeat(51);
                UpdateItem request = new UpdateItem(longName, "Valid description", true);
                Set<ConstraintViolation<UpdateItem>> violations = validator.validate(request);

                assertThat(violations).hasSize(1);
                assertThat(violations.iterator().next().getMessage())
                        .isEqualTo("Item title must be no longer than 50 characters");
            }

            @Test
            @DisplayName("UpdateItem with @Pattern violation in description -> should detect invalid characters")
            void updateItem_shouldHavePatternViolationForInvalidDescriptionCharacters() {
                UpdateItem request = new UpdateItem("Valid name", "Description[]", true);
                Set<ConstraintViolation<UpdateItem>> violations = validator.validate(request);

                assertThat(violations).hasSize(1);
                assertThat(violations.iterator().next().getMessage())
                        .isEqualTo("Description contains invalid characters");
            }

            @Test
            @DisplayName("UpdateItem with @Size violation in description -> should detect too long description")
            void updateItem_shouldHaveSizeViolationForTooLongDescription() {
                String longDescription = "A".repeat(301);
                UpdateItem request = new UpdateItem("Valid name", longDescription, true);
                Set<ConstraintViolation<UpdateItem>> violations = validator.validate(request);

                assertThat(violations).hasSize(1);
                assertThat(violations.iterator().next().getMessage())
                        .isEqualTo("Description must be no longer than 300 characters");
            }

            @Test
            @DisplayName("UpdateItem with all possible violations -> should have all violations")
            void updateItem_withAllPossibleViolations_shouldHaveAllViolations() {
                String longName = "Name@".repeat(11);
                String longDescription = "Desc[]".repeat(51);
                UpdateItem request = new UpdateItem(longName, longDescription, true);
                Set<ConstraintViolation<UpdateItem>> violations = validator.validate(request);

                assertThat(violations).hasSize(4);
                assertThat(violations)
                        .extracting(ConstraintViolation::getMessage)
                        .containsExactlyInAnyOrder(
                                "Item title contains invalid characters",
                                "Item title must be no longer than 50 characters",
                                "Description contains invalid characters",
                                "Description must be no longer than 300 characters"
                        );
            }
        }

        @Nested
        @DisplayName("CreateComment validation")
        class CreateCommentValidationTest {

            @Test
            @DisplayName("Valid CreateComment -> should have no violations")
            void createComment_withValidData_shouldHaveNoViolations() {
                CreateComment request = new CreateComment("Great item!");
                Set<ConstraintViolation<CreateComment>> violations = validator.validate(request);

                assertThat(violations).isEmpty();
            }

            @Test
            @DisplayName("CreateComment with blank text -> should have @NotBlank violation")
            void createComment_withBlankText_shouldHaveNotBlankViolation() {
                CreateComment request = new CreateComment("");
                Set<ConstraintViolation<CreateComment>> violations = validator.validate(request);

                assertThat(violations).hasSize(1);
                assertThat(violations.iterator().next().getMessage())
                        .isEqualTo("Comment cannot be empty");
            }

            @Test
            @DisplayName("CreateComment with too long text -> should have @Size violation")
            void createComment_withTooLongText_shouldHaveSizeViolation() {
                String longText = "A".repeat(501);
                CreateComment request = new CreateComment(longText);
                Set<ConstraintViolation<CreateComment>> violations = validator.validate(request);

                assertThat(violations).hasSize(1);
                assertThat(violations.iterator().next().getMessage())
                        .isEqualTo("Comment must be no longer than 500 characters");
            }
        }

    }

    @Nested
    @DisplayName("Booking DTO validation")
    class BookingDtoValidationTest {

        @Nested
        @DisplayName("CreateBookingRequest validation")
        class CreateBookingRequestValidationTest {

            @Test
            @DisplayName("Valid CreateBookingRequest -> should have no violations")
            void createBookingRequest_withValidData_shouldHaveNoViolations() {
                LocalDateTime start = LocalDateTime.now().plusDays(1);
                LocalDateTime end = LocalDateTime.now().plusDays(2);
                CreateBookingRequest request = new CreateBookingRequest(1L, start, end);

                Set<ConstraintViolation<CreateBookingRequest>> violations = validator.validate(request);

                assertThat(violations).isEmpty();
            }

            @Test
            @DisplayName("CreateBookingRequest with null itemId -> should have @NotNull violation")
            void createBookingRequest_withNullItemId_shouldHaveNotNullViolation() {
                LocalDateTime start = LocalDateTime.now().plusDays(1);
                LocalDateTime end = LocalDateTime.now().plusDays(2);
                CreateBookingRequest request = new CreateBookingRequest(null, start, end);

                Set<ConstraintViolation<CreateBookingRequest>> violations = validator.validate(request);

                assertThat(violations).hasSize(1);
                assertThat(violations.iterator().next().getMessage())
                        .isEqualTo("Item ID is required");
            }

            @Test
            @DisplayName("CreateBookingRequest with non-positive itemId -> should have @Positive violation")
            void createBookingRequest_withNonPositiveItemId_shouldHavePositiveViolation() {
                LocalDateTime start = LocalDateTime.now().plusDays(1);
                LocalDateTime end = LocalDateTime.now().plusDays(2);
                CreateBookingRequest request = new CreateBookingRequest(0L, start, end);

                Set<ConstraintViolation<CreateBookingRequest>> violations = validator.validate(request);

                assertThat(violations).hasSize(1);
                assertThat(violations.iterator().next().getMessage())
                        .containsIgnoringCase("greater than 0");
            }

            @Test
            @DisplayName("CreateBookingRequest with null start -> should have @NotNull violation")
            void createBookingRequest_withNullStart_shouldHaveNotNullViolation() {
                LocalDateTime end = LocalDateTime.now().plusDays(2);
                CreateBookingRequest request = new CreateBookingRequest(1L, null, end);

                Set<ConstraintViolation<CreateBookingRequest>> violations = validator.validate(request);

                assertThat(violations).hasSize(1);
                assertThat(violations.iterator().next().getMessage())
                        .isEqualTo("Start date is required");
            }

            @Test
            @DisplayName("CreateBookingRequest with past start -> should have @FutureOrPresent violation")
            void createBookingRequest_withPastStart_shouldHaveFutureOrPresentViolation() {
                LocalDateTime pastStart = LocalDateTime.now().minusDays(1);
                LocalDateTime end = LocalDateTime.now().plusDays(2);
                CreateBookingRequest request = new CreateBookingRequest(1L, pastStart, end);

                Set<ConstraintViolation<CreateBookingRequest>> violations = validator.validate(request);

                assertThat(violations).hasSize(1);
                assertThat(violations.iterator().next().getMessage())
                        .isEqualTo("Start date cannot be in past");
            }

            @Test
            @DisplayName("CreateBookingRequest with null end -> should have @NotNull violation")
            void createBookingRequest_withNullEnd_shouldHaveNotNullViolation() {
                LocalDateTime start = LocalDateTime.now().plusDays(1);
                CreateBookingRequest request = new CreateBookingRequest(1L, start, null);

                Set<ConstraintViolation<CreateBookingRequest>> violations = validator.validate(request);

                assertThat(violations).hasSize(1);
                assertThat(violations.iterator().next().getMessage())
                        .isEqualTo("End date is required");
            }

            @Test
            @DisplayName("CreateBookingRequest with non-future end -> should have @Future violation")
            void createBookingRequest_withNonFutureEnd_shouldHaveFutureViolation() {
                LocalDateTime start = LocalDateTime.now().plusDays(1);
                LocalDateTime pastEnd = LocalDateTime.now().minusDays(1);
                CreateBookingRequest request = new CreateBookingRequest(1L, start, pastEnd);

                Set<ConstraintViolation<CreateBookingRequest>> violations = validator.validate(request);

                assertThat(violations).hasSize(1);
                assertThat(violations.iterator().next().getMessage())
                        .isEqualTo("End date must be after start date");
            }

            @Test
            @DisplayName("CreateBookingRequest with all possible violations -> should have all violations")
            void createBookingRequest_withAllPossibleViolations_shouldHaveAllViolations() {
                LocalDateTime pastStart = LocalDateTime.now().minusDays(2);
                LocalDateTime pastEnd = LocalDateTime.now().minusDays(1);
                CreateBookingRequest request = new CreateBookingRequest(0L, pastStart, pastEnd);

                Set<ConstraintViolation<CreateBookingRequest>> violations = validator.validate(request);

                assertThat(violations).hasSize(3);
                assertThat(violations)
                        .extracting(ConstraintViolation::getMessage)
                        .containsExactlyInAnyOrder(
                                "Item ID must be greater than 0",
                                "Start date cannot be in past",
                                "End date must be after start date"
                        );
            }
        }
    }

    @Nested
    @DisplayName("Item Request DTO validation")
    class ItemRequestDtoValidationTest {

        @Nested
        @DisplayName("CreateItemRequest validation")
        class CreateItemRequestValidationTest {

            @Test
            @DisplayName("Valid CreateItemRequest -> should have no violations")
            void createItemRequest_withValidData_shouldHaveNoViolations() {
                CreateItemRequest request = new CreateItemRequest("Need a power drill");
                Set<ConstraintViolation<CreateItemRequest>> violations = validator.validate(request);

                assertThat(violations).isEmpty();
            }

            @Test
            @DisplayName("CreateItemRequest with blank description -> should have @NotBlank violation")
            void createItemRequest_withBlankDescription_shouldHaveNotBlankViolation() {
                CreateItemRequest request = new CreateItemRequest("");
                Set<ConstraintViolation<CreateItemRequest>> violations = validator.validate(request);

                assertThat(violations).hasSize(1);
                assertThat(violations.iterator().next().getMessage())
                        .isEqualTo("не должно быть пустым");
            }

            @Test
            @DisplayName("CreateItemRequest with too long description -> should have @Size violation")
            void createItemRequest_withTooLongDescription_shouldHaveSizeViolation() {
                String longDescription = "A".repeat(501);
                CreateItemRequest request = new CreateItemRequest(longDescription);
                Set<ConstraintViolation<CreateItemRequest>> violations = validator.validate(request);

                assertThat(violations).hasSize(1);
                assertThat(violations.iterator().next().getMessage())
                        .isEqualTo("Description must be no longer than 500 characters");
            }
        }
    }
}
