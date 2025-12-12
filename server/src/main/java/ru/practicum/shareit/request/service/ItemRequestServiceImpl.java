package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestExtendedDto;
import ru.practicum.shareit.request.dto.creation.CreateItemRequest;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.sharing.EntityFinder;
import ru.practicum.shareit.sharing.EntityType;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final EntityFinder entityFinder;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemMapper itemMapper;

    @Override
    public ItemRequestDto create(Long authorId, CreateItemRequest createRequest) {
        User author = entityFinder.findOrThrow(userRepository, authorId, EntityType.USER);
        ItemRequest newItemRequest = itemRequestMapper.toItem(createRequest);
        newItemRequest.setAuthor(author);
        newItemRequest = itemRequestRepository.save(newItemRequest);
        return itemRequestMapper.toItemRequestDto(newItemRequest);
    }

    @Override
    public List<ItemRequestExtendedDto> getAllByAuthor(Long authorId) {
        entityFinder.findOrThrow(userRepository, authorId, EntityType.USER);

        List<ItemRequest> itemRequests = itemRequestRepository.findAllByAuthorIdOrderByCreatedDesc(authorId);

        if (itemRequests.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> requestIds = itemRequests.stream()
                .map(ItemRequest::getId)
                .toList();

        Map<Long, List<ItemDto>> items = itemRepository.findItemsGroupedByRequestIdsGrouped(requestIds)
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .map(itemMapper::toItemDto)
                                .toList()
                ));

        return itemRequests.stream()
                .map(itemRequest -> new ItemRequestExtendedDto(
                        itemRequest.getId(),
                        itemRequest.getDescription(),
                        itemRequest.getCreated(),
                        items.get(itemRequest.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAll() {
        List<ItemRequest> itemRequests = itemRequestRepository.findAll();
        return itemRequests.stream()
                .map(itemRequestMapper::toItemRequestDto)
                .toList();
    }

    @Override
    public ItemRequestExtendedDto getById(Long id) {
        ItemRequest itemRequest = entityFinder.findOrThrow(itemRequestRepository, id, EntityType.ITEM_REQUEST);
        List<ItemDto> items = itemRepository.findItemsByItemRequestId(id).stream()
                .map(itemMapper::toItemDto)
                .toList();

        return new ItemRequestExtendedDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                items);
    }
}
