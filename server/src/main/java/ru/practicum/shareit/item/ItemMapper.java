package ru.practicum.shareit.item;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

@Component
@NoArgsConstructor
public class ItemMapper {
    public Item toItem(NewItemRequest request) {
        Item item = new Item();
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setAvailable(request.getAvailable());

        return item;
    }

    public ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setOwner(item.getOwner());

        return itemDto;
    }

    public Item updateItemFields(Item item, UpdateItemRequest request) {
        if (request.getName() != null && !request.getName().isBlank()) {
            item.setName(request.getName());
        }
        if (request.getDescription() != null && !request.getDescription().isBlank()) {
            item.setDescription(request.getDescription());
        }
        if (request.getAvailable() != null) {
            item.setAvailable(request.getAvailable());
        }

        return item;
    }

    public ItemBookingDto toItemBookingDto(Item item) {
        ItemBookingDto itemBookingDto = new ItemBookingDto();
        itemBookingDto.setId(item.getId());
        itemBookingDto.setName(item.getName());

        return itemBookingDto;
    }

    public ItemWithDateDto toItemWithDateDto(Item item) {
        ItemWithDateDto itemWithDateDto = new ItemWithDateDto();
        itemWithDateDto.setId(item.getId());
        itemWithDateDto.setName(item.getName());
        itemWithDateDto.setDescription(item.getDescription());
        itemWithDateDto.setAvailable(item.getAvailable());
        itemWithDateDto.setOwner(item.getOwner());

        return itemWithDateDto;
    }

    public ItemWithCommentsDto toItemWithCommentsDto(Item item) {
        ItemWithCommentsDto itemWithCommentsDto = new ItemWithCommentsDto();
        itemWithCommentsDto.setId(item.getId());
        itemWithCommentsDto.setName(item.getName());
        itemWithCommentsDto.setDescription(item.getDescription());
        itemWithCommentsDto.setAvailable(item.getAvailable());
        itemWithCommentsDto.setOwner(item.getOwner());

        return itemWithCommentsDto;
    }
}
