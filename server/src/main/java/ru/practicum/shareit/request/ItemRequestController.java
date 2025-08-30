package ru.practicum.shareit.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestController {
    final ItemRequestServiceImpl itemRequestService;
    static final String HEADER_USER_ID = "X-Sharer-User-Id";
    static final String NOT_FOUND_HEADER_USER_ID_MESSAGE = "ID пользователя должно быть указано в заголовке";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto create(@RequestHeader(value = HEADER_USER_ID, required = false)
                             @NotNull(message = NOT_FOUND_HEADER_USER_ID_MESSAGE) Long userId,
                                 @RequestBody NewItemRequest request) {
        log.info("Запрос на добавление запроса на предмет {}", request);
        return itemRequestService.saveRequest(userId, request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestDto> findByRequestorId(@RequestHeader(value = HEADER_USER_ID, required = false)
                                                      @NotNull(message = NOT_FOUND_HEADER_USER_ID_MESSAGE) Long userId) {
        log.info("Запрос на получение списка запросов от пользователя с ID - {}", userId);
        return itemRequestService.findItemRequestsByRequestorId(userId);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestDto> findRequestsOtherUsers(@RequestHeader(value = HEADER_USER_ID, required = false)
                                                           @NotNull(message = NOT_FOUND_HEADER_USER_ID_MESSAGE) Long userId) {
        log.info("Запрос на получение списка запросов пользователей не с ID - {}", userId);
        return itemRequestService.findItemRequestsOtherUsers(userId);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ItemRequestDtoWithItems findRequestById(@PathVariable Long id) {
        log.info("Запрос на получение запроса с ID - {}", id);
        return itemRequestService.findById(id);
    }
}
