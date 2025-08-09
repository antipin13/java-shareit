package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;


import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Validated
public class ItemController {
    final ItemService itemService;
    static final String HEADER_USER_ID = "X-Sharer-User-Id";
    static final String NOT_FOUND_HEADER_USER_ID_MESSAGE = "ID пользователя должно быть указано в заголовке";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestHeader(value = HEADER_USER_ID, required = false)
                              @NotNull(message = NOT_FOUND_HEADER_USER_ID_MESSAGE) Long userId,
                       @Valid @RequestBody NewItemRequest request) {
        log.info("Запрос на добавление предмета {}", request);
        return itemService.saveItem(userId, request);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto update(@RequestHeader(HEADER_USER_ID)
                              @NotNull(message = NOT_FOUND_HEADER_USER_ID_MESSAGE) Long userId,
                          @Valid @RequestBody UpdateItemRequest request,
                          @PathVariable Long id) {
        log.info("Запрос на обновление предмета - {}", request);
        return itemService.updateItem(userId, request, id);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<ItemWithCommentsDto> findById(@PathVariable Long id, @RequestHeader(HEADER_USER_ID) Long userId) {
        return Optional.ofNullable(itemService.findItemById(userId, id));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemWithDateDto> findItemsByUserId(@RequestHeader(HEADER_USER_ID) Long userId) {
        return itemService.findItemsByUserId(userId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> searchItems(@RequestHeader(HEADER_USER_ID) Long userId, @RequestParam String text) {
        return itemService.findItemsByTextAndAvailable(userId, text);
    }

    @PostMapping("/{id}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto create(@PathVariable Long id, @RequestHeader(HEADER_USER_ID) Long userId,
                             @Valid @RequestBody NewCommentRequest request) {
        return itemService.saveComment(id, userId, request);
    }
}
