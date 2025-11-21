//package ru.practicum.shareit.items;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.TestPropertySource;
//import ru.practicum.shareit.exceptions.ItemOwnershipException;
//import ru.practicum.shareit.exceptions.NotFoundException;
//import ru.practicum.shareit.item.dto.CreateItemRequest;
//import ru.practicum.shareit.item.dto.ItemDto;
//import ru.practicum.shareit.item.dto.UpdateItemRequest;
//import ru.practicum.shareit.item.model.Item;
//
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//import static ru.practicum.shareit.items.ItemsTestConfig.*;
//
//@SpringBootTest(classes = ItemsTestConfig.class)
//@TestPropertySource(locations = "classpath:application-test.properties")
//@DisplayName("ItemService CRUD operations")
//public class ItemServiceTests {
//    @Autowired
//    private ItemServiceImplTest itemService;
//
//    @Autowired
//    private Item firstTestItem;
//
//    @Autowired
//    private Item secondTestItem;
//
//    @Autowired
//    private Item thirdTestItem;
//
//    @Autowired
//    private CreateItemRequest createRequest;
//
//    @Autowired
//    private UpdateItemRequest updateRequest;
//
//    @BeforeEach
//    public void setUp() {
//        itemService.resetTestData();
//    }
//
//    @Nested
//    @DisplayName("Find Item Operations")
//    class FindItemTests {
//        @Test
//        @DisplayName("Should find existing item by ID")
//        void findById_WhenItemExists_ReturnsItem() {
//            ItemDto result = itemService.findById(ALL_ITEM_IDS[1]);
//
//            assertThat(result)
//                    .usingRecursiveComparison()
//                    .isEqualTo(secondTestItem);
//        }
//
//        @Test
//        @DisplayName("Should throw NotFoundException when searching non-existent item")
//        void findById_WhenItemNotExists_ThrowsNotFoundException() {
//            long nonExistentId = 999L;
//
//            assertThatThrownBy(() -> itemService.findById(nonExistentId))
//                    .isInstanceOf(NotFoundException.class)
//                    .hasMessageMatching("Item id=%d not found".formatted(nonExistentId));
//        }
//
//        @Test
//        @DisplayName("Should find all items owned by the user")
//        void findByOwnerId_WhenUserExists_ReturnsAllUserItems() {
//            List<Item> expectedItems = List.of(firstTestItem, secondTestItem);
//            List<ItemDto> actualItems = itemService.findByOwnerId(TEST_ITEMS_1_AND_2_OWNER_ID);
//
//            assertThat(actualItems)
//                    .usingRecursiveComparison()
//                    .isEqualTo(expectedItems);
//        }
//
//        @Test
//        @DisplayName("Should throw NotFoundException when user does not exist")
//        void findByOwnerId_WhenUserNotExists_ThrowsNotFoundException() {
//
//            assertThatThrownBy(() -> itemService.findByOwnerId(TEST_NON_EXISTENT_ID))
//                    .isInstanceOf(NotFoundException.class)
//                    .hasMessageMatching("User id=%d not found".formatted(TEST_NON_EXISTENT_ID));
//        }
//
//        @Test
//        @DisplayName("Should return empty list when user exists but has no items")
//        void findByOwnerId_WhenUserHasNoItems_ReturnsEmptyList() {
//            List<ItemDto> result = itemService.findByOwnerId(TEST_NO_ITEMS_OWNER_ID);
//
//            assertThat(result).isEmpty();
//        }
//
//        @Test
//        @DisplayName("Should return available items when query matches names and descriptions")
//        void findByQuery_WhenQueryMatchesNamesAndDescriptions_ReturnsAvailableItems() {
//            String query = "Some machine";
//            List<Item> expectedItems = List.of(firstTestItem, thirdTestItem);
//            List<ItemDto> actualItems = itemService.findByQuery(query);
//
//            assertThat(actualItems)
//                    .usingRecursiveComparison()
//                    .isEqualTo(expectedItems);
//        }
//
//        @Test
//        @DisplayName("Should return matching items regardless of query letter case")
//        void findByQuery_whenQueryHasMixedCase_returnsItemsCaseInsensitive() {
//            String query = "Some MAcHiNe";
//            List<Item> expectedItems = List.of(firstTestItem, thirdTestItem);
//            List<ItemDto> actualItems = itemService.findByQuery(query);
//
//            assertThat(actualItems)
//                    .usingRecursiveComparison()
//                    .isEqualTo(expectedItems);
//        }
//
//        @Test
//        @DisplayName("Should return only available items when some matching items are unavailable")
//        void findByQuery_WhenSomeItemsUnavailable_ReturnsOnlyAvailableItems() {
//            itemService.getTestItemRepository().getItems().get(ALL_ITEM_IDS[0]).setAvailable(false);
//            String query = "Some machine";
//            List<Item> expectedItems = List.of(thirdTestItem);
//            List<ItemDto> actualItems = itemService.findByQuery(query);
//
//            assertThat(actualItems)
//                    .usingRecursiveComparison()
//                    .isEqualTo(expectedItems);
//        }
//
//        @Test
//        @DisplayName("Should return empty list when search query doesn't match any items")
//        void findByQuery_whenHasNoMatchingWords_returnsEmptyList() {
//            String query = "Household appliances";
//            List<ItemDto> actualItems = itemService.findByQuery(query);
//
//            assertThat(actualItems).isEmpty();
//        }
//    }
//
//    @Nested
//    @DisplayName("Create Item Operations")
//    class CreateItemTests {
//        @Test
//        @DisplayName("Should create new item with valid data and correct ID")
//        void create_withValidData_ReturnsItemWithCorrectId() {
//            ItemDto createdItem = itemService.create(createRequest, TEST_NO_ITEMS_OWNER_ID);
//
//            assertThat(createdItem)
//                    .extracting(ItemDto::id, ItemDto::name, ItemDto::description, ItemDto::available)
//                    .containsExactly(TEST_ITEM_GENERATOR_DEFAULT + 1, TEST_NEW_ITEM_NAME, TEST_NEW_ITEM_DESCRIPTION,
//                            true);
//        }
//
//        @Test
//        @DisplayName("Should create new item with correct owner")
//        void create_withValidData_ReturnsItemWithCorrectOwner() {
//            ItemDto createdItem = itemService.create(createRequest, TEST_NO_ITEMS_OWNER_ID);
//            List<ItemDto> ownerItems = itemService.findByOwnerId(TEST_NO_ITEMS_OWNER_ID);
//
//            assertThat(ownerItems)
//                    .hasSize(1)
//                    .first()
//                    .usingRecursiveComparison()
//                    .isEqualTo(createdItem);
//        }
//
//        @Test
//        @DisplayName("Should throw NotFoundException when creating item with non-existent user")
//        void create_WithNonExistentUser_ThrowsNotFoundException() {
//
//            assertThatThrownBy(() -> itemService.create(createRequest, TEST_NON_EXISTENT_ID))
//                    .isInstanceOf(NotFoundException.class)
//                    .hasMessageMatching("User id=%d not found".formatted(TEST_NON_EXISTENT_ID));
//        }
//    }
//
//    @Nested
//    @DisplayName("Update Item Operations")
//    class UpdateItemTests {
//        @Test
//        @DisplayName("Should update all fields when all fields provided")
//        void update_WhenAllFieldsProvided_UpdatesAllFields() {
//            ItemDto updatedItem = itemService.update(updateRequest, ALL_ITEM_IDS[2], TEST_ITEM_3_OWNER_ID);
//
//            assertThat(updatedItem)
//                    .extracting(ItemDto::id, ItemDto::name, ItemDto::description, ItemDto::available)
//                    .containsExactly(ALL_ITEM_IDS[2], TEST_NEW_ITEM_NAME, TEST_NEW_ITEM_DESCRIPTION,
//                            true);
//        }
//
//        @Test
//        @DisplayName("Should throw ItemOwnershipException when non-owner tries to update item")
//        void update_ByNonOwner_ThrowsItemOwnershipException() {
//            ItemDto updatedItem = itemService.update(updateRequest, ALL_ITEM_IDS[2], TEST_ITEM_3_OWNER_ID);
//
//            assertThatThrownBy(() -> itemService.update(updateRequest, ALL_ITEM_IDS[2], TEST_ITEMS_1_AND_2_OWNER_ID))
//                    .isInstanceOf(ItemOwnershipException.class)
//                    .hasMessageMatching("User id=%d is not owned by item id=%d"
//                            .formatted(TEST_ITEMS_1_AND_2_OWNER_ID, ALL_ITEM_IDS[2]));
//        }
//
//        @Test
//        @DisplayName("Should update only name when description and available are null")
//        void update_WhenOnlyNameProvided_UpdatesOnlyName() {
//            UpdateItemRequest request = new UpdateItemRequest(TEST_NEW_ITEM_NAME, null, null);
//            long itemId = ALL_ITEM_IDS[0];
//            ItemDto updatedItem = itemService.update(request, itemId, TEST_ITEMS_1_AND_2_OWNER_ID);
//
//            assertThat(updatedItem)
//                    .extracting(ItemDto::id, ItemDto::name, ItemDto::description, ItemDto::available)
//                    .containsExactly(itemId, TEST_NEW_ITEM_NAME, TEST_ITEM_1_DESCRIPTION,
//                            true);
//        }
//
//        @Test
//        @DisplayName("Should update description and available when name is null")
//        void update_WhenNameIsNull_UpdatesDescriptionAndAvailable() {
//            UpdateItemRequest request = new UpdateItemRequest(null, TEST_NEW_ITEM_DESCRIPTION, false);
//            long itemId = ALL_ITEM_IDS[0];
//            ItemDto updatedItem = itemService.update(request, itemId, TEST_ITEMS_1_AND_2_OWNER_ID);
//
//            assertThat(updatedItem)
//                    .extracting(ItemDto::id, ItemDto::name, ItemDto::description, ItemDto::available)
//                    .containsExactly(itemId, TEST_ITEM_1_NAME, TEST_NEW_ITEM_DESCRIPTION,
//                            false);
//        }
//    }
//
//    @Nested
//    @DisplayName("Delete Item Operations")
//    class DeleteItemTests {
//        @Test
//        @DisplayName("Should delete item and throw NotFoundException when searching deleted item")
//        void delete_WhenOwnerValid_ItemBecomesInaccessible() {
//            long itemId = ALL_ITEM_IDS[0];
//            itemService.delete(itemId, TEST_ITEMS_1_AND_2_OWNER_ID);
//
//            assertThatThrownBy(() -> itemService.findById(itemId))
//                    .isInstanceOf(NotFoundException.class)
//                    .hasMessageMatching("Item id=%d not found".formatted(itemId));
//        }
//
//        @Test
//        @DisplayName("Should throw ItemOwnershipException when trying to delete another user's item")
//        void delete_WhenOwnerInvalid_ThrowsItemOwnershipException() {
//            long itemId = ALL_ITEM_IDS[0];
//            long ownerId = TEST_ITEM_3_OWNER_ID;
//
//            assertThatThrownBy(() -> itemService.delete(itemId, ownerId))
//                    .isInstanceOf(ItemOwnershipException.class)
//                    .hasMessageMatching("User id=%d is not owned by item id=%d"
//                            .formatted(ownerId, itemId));
//        }
//
//        @Test
//        @DisplayName("Should throw NotFoundException when trying to delete non-existent item")
//        void delete_WhenItemNotExist_ThrowsNotFoundException() {
//            long itemId = TEST_NON_EXISTENT_ID;
//
//            assertThatThrownBy(() -> itemService.delete(itemId, TEST_ITEMS_1_AND_2_OWNER_ID))
//                    .isInstanceOf(NotFoundException.class)
//                    .hasMessageMatching("Item id=%d not found".formatted(itemId));
//        }
//    }
//}
