package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto saveRequest(Long userId, NewItemRequest request);

    List<ItemRequestDto> findItemRequestsByRequestorId(Long requestorId);

    List<ItemRequestDto> findItemRequestsOtherUsers(Long requestorId);

    ItemRequestDtoWithItems findById(Long requestId);
}
