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
import ru.practicum.shareit.user.UserService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemServiceImpl implements ItemService {
    final ItemRepository itemRepository;
    final UserService userService;

    public ItemServiceImpl(@Qualifier("InMemoryStorage") ItemRepository itemRepository, UserService userService) {
        this.itemRepository = itemRepository;
        this.userService = userService;
    }

    @Override
    public ItemDto saveItem(Long userId, NewItemRequest request) {
        userService.findUserById(userId);

        Item item = ItemMapper.toItem(request);
        item.setOwner(userId);

        ItemValidator.validateItem(item);

        item = itemRepository.save(item);

        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(Long userId, UpdateItemRequest request, Long id) {
        userService.findUserById(userId);

        Item existingItem = itemRepository.findItemById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Предмет с ID - %d не найден", userId)));

        existingItem = ItemMapper.updateItemFields(existingItem, request);

        ItemValidator.validateItem(existingItem);

        existingItem = itemRepository.update(existingItem);

        return ItemMapper.toItemDto(existingItem);
    }

    @Override
    public ItemDto findItemById(Long userId, Long id) {
        userService.findUserById(userId);

        return itemRepository.findItemById(id)
                .map(ItemMapper::toItemDto)
                .orElseThrow(() -> new NotFoundException(String.format("Предмет с ID - %d не найден", id)));
    }

    @Override
    public List<ItemDto> findItemsByUserId(Long userId) {
        userService.findUserById(userId);

        return itemRepository.findItemsByUserId(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> findItemsByTextAndAvailable(Long userId, String text) {
        userService.findUserById(userId);

        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.findItemsByTextAndAvailable(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
