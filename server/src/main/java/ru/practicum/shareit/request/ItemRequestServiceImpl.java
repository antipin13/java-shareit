package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    ItemRequestRepository itemRequestRepository;
    UserRepository userRepository;
    ItemRepository itemRepository;
    ItemRequestMapper itemRequestMapper;
    static final String NOT_FOUND_MESSAGE_USER = "Пользователь с ID - %d не найден";

    @Override
    public ItemRequestDto saveRequest(Long userId, NewItemRequest request) {
        User user = getUserOrThrow(userId);

        ItemRequest itemRequest = itemRequestMapper.toItemRequest(request);
        itemRequest.setRequestor(user);

        itemRequest = itemRequestRepository.save(itemRequest);

        return itemRequestMapper.toItemRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> findItemRequestsByRequestorId(Long requestorId) {
        User user = getUserOrThrow(requestorId);

        return itemRequestRepository.findByRequestorIdOrderByCreatedDesc(requestorId).stream()
                .map(itemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> findItemRequestsOtherUsers(Long requestorId) {
        User user = getUserOrThrow(requestorId);

        return itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(requestorId).stream()
                .map(itemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDtoWithItems findById(Long requestId) {
        ItemRequestDtoWithItems itemRequestDtoWithItems = itemRequestRepository.findById(requestId)
                .map(itemRequestMapper::toItemRequestDtoWithItems)
                .orElseThrow(() -> new NotFoundException(String.format("Запрос с ID - @d не найден", requestId)));

        List<Item> itemsByRequest = itemRepository.findByRequestId(requestId);
        itemRequestDtoWithItems.setItems(itemsByRequest);

        return itemRequestDtoWithItems;
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_MESSAGE_USER, userId)));
    }
}
