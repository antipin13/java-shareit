package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    final ItemRepository itemRepository;
    final UserRepository userRepository;
    final BookingRepository bookingRepository;
    final CommentRepository commentRepository;
    final ItemMapper itemMapper;
    final CommentMapper commentMapper;
    static final String NOT_FOUND_MESSAGE_ITEM = "Предмет с ID - %d не найден";
    static final String NOT_FOUND_MESSAGE_USER = "Пользователь с ID - %d не найден";

    @Override
    public ItemDto saveItem(Long userId, NewItemRequest request) {
        User user = getUserOrThrow(userId);

        Item item = itemMapper.toItem(request);
        item.setOwner(user);

        item = itemRepository.save(item);

        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(Long userId, UpdateItemRequest request, Long itemId) {
        User user = getUserOrThrow(userId);

        Item existingItem = getItemOrThrow(itemId);

        existingItem = itemMapper.updateItemFields(existingItem, request);

        existingItem = itemRepository.save(existingItem);

        return itemMapper.toItemDto(existingItem);
    }

    @Override
    public ItemWithCommentsDto findItemById(Long userId, Long itemId) {
        User user = getUserOrThrow(userId);

        Item item = getItemOrThrow(itemId);

        List<CommentDto> comments = commentRepository.findByItemId(itemId).stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());

        ItemWithCommentsDto itemWithCommentsDto = itemMapper.toItemWithCommentsDto(item);
        itemWithCommentsDto.setComments(comments);

        return itemWithCommentsDto;
    }

    @Override
    public List<ItemWithDateDto> findItemsByUserId(Long userId) {
        User user = getUserOrThrow(userId);
        List<Item> items = itemRepository.findByOwnerId(userId);

        return items.stream()
                .map(item -> {
                    List<Booking> bookings = bookingRepository.findByItemId(item.getId());

                    Optional<Booking> lastBooking = bookings.stream()
                            .max(Comparator.comparing(Booking::getEnd));

                    Optional<Booking> nextBooking = bookings.stream()
                            .min(Comparator.comparing(Booking::getStart));

                    ItemWithDateDto itemDto = itemMapper.toItemWithDateDto(item);

                    itemDto.setLastBookingDate(lastBooking
                            .map(Booking::getEnd)
                            .orElse(null));
                    itemDto.setNextBookingDate(nextBooking
                            .map(Booking::getStart)
                            .orElse(null));

                    return itemDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> findItemsByTextAndAvailable(Long userId, String text) {
        User user = getUserOrThrow(userId);

        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.findByText(text).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto saveComment(Long itemId, Long userId, NewCommentRequest request) {
        Booking booking = bookingRepository.findByItemIdAndBookerId(itemId, userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Пользователь с ID - %d не брал вещь в аренду", userId)));

        Item item = getItemOrThrow(itemId);

        User user = getUserOrThrow(userId);

        if (booking.getEnd().isAfter(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Дата бронирования еще не прошла");
        }

        Comment comment = commentMapper.toComment(request);
        comment.setItem(item);
        comment.setAuthor(user);

        comment = commentRepository.save(comment);

        return commentMapper.toCommentDto(comment);
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_MESSAGE_USER, userId)));
    }

    private Item getItemOrThrow(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_MESSAGE_ITEM, itemId)));
    }
}
