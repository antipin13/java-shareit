package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
    ItemDto saveItem(Long userId, NewItemRequest request);

    ItemDto updateItem(Long userId, UpdateItemRequest request, Long id);

    ItemWithCommentsDto findItemById(Long userId, Long id);

    List<ItemWithDateDto> findItemsByUserId(Long userId);

    List<ItemDto> findItemsByTextAndAvailable(Long userId, String text);

    CommentDto saveComment(Long itemId, Long userId, NewCommentRequest request);
}
