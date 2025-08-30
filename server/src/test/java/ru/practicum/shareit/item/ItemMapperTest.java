package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithCommentsDto;
import ru.practicum.shareit.item.dto.ItemWithDateDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemMapperTest {
    final ItemMapper itemMapper = new ItemMapper();

    @Test
    public void toItem() {
        NewItemRequest request = new NewItemRequest();
        request.setName("Test Item");
        request.setDescription("Test Description");
        request.setAvailable(true);

        Item item = new ItemMapper().toItem(request);

        assertEquals("Test Item", item.getName());
        assertEquals("Test Description", item.getDescription());
        assertTrue(item.getAvailable());
    }

    @Test
    public void toItemDto() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Test Name");
        item.setDescription("Test Description");
        item.setAvailable(true);
        User owner = new User();
        owner.setId(10L);
        item.setOwner(owner);

        ItemDto itemDto = new ItemMapper().toItemDto(item);

        assertEquals(1L, itemDto.getId());
        assertEquals("Test Name", itemDto.getName());
        assertEquals("Test Description", itemDto.getDescription());
        assertTrue(itemDto.getAvailable());
        assertEquals(10L, itemDto.getOwner().getId());
    }

    @Test
    public void updateItemFields() {
        Item item = new Item();
        item.setName("Old Name");
        item.setDescription("Old Description");
        item.setAvailable(false);

        UpdateItemRequest request = new UpdateItemRequest();
        request.setName("New Name");
        request.setDescription("New Description");
        request.setAvailable(true);

        Item updatedItem = itemMapper.updateItemFields(item, request);

        assertEquals("New Name", updatedItem.getName());
        assertEquals("New Description", updatedItem.getDescription());
        assertTrue(updatedItem.getAvailable());
    }

    @Test
    public void testUpdateItemFieldsWithNulls() {
        Item item = new Item();
        item.setName("Old Name");
        item.setDescription("Old Description");
        item.setAvailable(false);

        UpdateItemRequest request = new UpdateItemRequest();
        request.setName(null);
        request.setDescription("");
        request.setAvailable(null);

        Item updatedItem = itemMapper.updateItemFields(item, request);

        assertEquals("Old Name", updatedItem.getName());
        assertEquals("Old Description", updatedItem.getDescription());
        assertFalse(updatedItem.getAvailable());
    }

    @Test
    public void toItemBookingDto() {
        Item item = new Item();
        item.setId(5L);
        item.setName("Test Item");

        ItemBookingDto dto = itemMapper.toItemBookingDto(item);

        assertEquals(5L, dto.getId());
        assertEquals("Test Item", dto.getName());
    }

    @Test
    public void testToItemWithDateDto() {
        Item item = new Item();
        item.setId(10L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        User owner = new User();
        owner.setId(20L);
        item.setOwner(owner);

        ItemWithDateDto dto = itemMapper.toItemWithDateDto(item);

        assertEquals(10L, dto.getId());
        assertEquals("Test Item", dto.getName());
        assertEquals("Test Description", dto.getDescription());
        assertTrue(dto.getAvailable());
        assertEquals(20L, dto.getOwner().getId());
    }

    @Test
    public void toItemWithCommentsDto() {
        Item item = new Item();
        item.setId(15L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        User owner = new User();
        owner.setId(25L);
        item.setOwner(owner);

        ItemWithCommentsDto dto = itemMapper.toItemWithCommentsDto(item);

        assertEquals(15L, dto.getId());
        assertEquals("Test Item", dto.getName());
        assertEquals("Test Description", dto.getDescription());
        assertTrue(dto.getAvailable());
        assertEquals(25L, dto.getOwner().getId());
    }
}