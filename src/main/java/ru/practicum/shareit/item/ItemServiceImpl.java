package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemServiceImpl implements ItemService {
    final ItemRepository itemRepository;
    final UserRepository userRepository;
    final ItemMapper itemMapper;
    final UserMapper userMapper;
    static final String NOT_FOUND_MESSAGE_ITEM = "Предмет с ID - %d не найден";
    static final String NOT_FOUND_MESSAGE_USER = "Пользователь с ID - %d не найден";

    public ItemServiceImpl(@Qualifier("InMemoryStorage") ItemRepository itemRepository,
                           @Qualifier("InMemoryStorage") UserRepository userRepository, ItemMapper itemMapper,
                           UserMapper userMapper) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.itemMapper = itemMapper;
        this.userMapper = userMapper;
    }

    @Override
    public ItemDto saveItem(Long userId, NewItemRequest request) {
        UserDto userDto = getUserOrThrow(userId);

        Item item = itemMapper.toItem(request);
        item.setOwner(userId);

        ItemValidator.validateItem(item);

        item = itemRepository.save(item);

        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(Long userId, UpdateItemRequest request, Long id) {
        UserDto userDto = getUserOrThrow(userId);

        Item existingItem = itemRepository.findItemById(id)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_MESSAGE_ITEM, userId)));

        existingItem = itemMapper.updateItemFields(existingItem, request);

        ItemValidator.validateItem(existingItem);

        existingItem = itemRepository.update(existingItem);

        return itemMapper.toItemDto(existingItem);
    }

    @Override
    public ItemDto findItemById(Long userId, Long id) {
        UserDto userDto = getUserOrThrow(userId);

        return itemRepository.findItemById(id)
                .map(itemMapper::toItemDto)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_MESSAGE_ITEM, id)));
    }

    @Override
    public List<ItemDto> findItemsByUserId(Long userId) {
        UserDto userDto = getUserOrThrow(userId);

        return itemRepository.findItemsByUserId(userId).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> findItemsByTextAndAvailable(Long userId, String text) {
        UserDto userDto = getUserOrThrow(userId);

        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.findItemsByTextAndAvailable(text).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private UserDto getUserOrThrow(Long userId) {
        return userRepository.findUserById(userId)
                .map(userMapper::toUserDto)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_MESSAGE_USER, userId)));
    }
}
