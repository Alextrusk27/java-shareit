package ru.practicum.shareit.request.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestExtendedDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.creation.CreateItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.sharing.HttpHeader;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@Slf4j
@Validated
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto createItemRequest(@RequestHeader(HttpHeader.USER_ID)
                                     @Positive(message = "User ID must be greater than 0")
                                     Long authorId,
                                     @RequestBody @Valid
                                     CreateItemRequest itemRequest) {
        log.info("Creating new item request '{}' from user id={}", itemRequest.description(), authorId);
        ItemRequestDto result = itemRequestService.create(authorId, itemRequest);
        log.info("Item request created with id={}", result.id());
        return result;
    }

    @GetMapping
    public List<ItemRequestExtendedDto> getItemRequestByAuthor(@RequestHeader(HttpHeader.USER_ID)
                                                  @Positive(message = "User ID must be greater than 0")
                                                  Long authorId) {
        log.info("Searching for item requests by user id={}", authorId);
        List<ItemRequestExtendedDto> result = itemRequestService.getAllByAuthor(authorId);
        log.info("Found {} item requests by user id={}", result.size(), authorId);
        return result;
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequests() {
        log.info("Searching for all item requests");
        List<ItemRequestDto> result = itemRequestService.getAll();
        log.info("Found {} item requests", result.size());
        return result;
    }

    @GetMapping("/{requestId}")
    public ItemRequestExtendedDto getItemRequest(@PathVariable
                                          @Positive(message = "Request ID must be greater than 0")
                                          Long requestId) {
        log.info("Searching for item request with id={}", requestId);
        ItemRequestExtendedDto result = itemRequestService.getById(requestId);
        log.info("Item request found with id={}", result.id());
        return result;
    }
}
