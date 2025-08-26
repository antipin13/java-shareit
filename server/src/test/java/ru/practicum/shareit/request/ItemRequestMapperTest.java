package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemRequestMapperTest {
    final ItemRequestMapper itemRequestMapper = new ItemRequestMapper();

    @Test
    void toItemRequest() {
        NewItemRequest request = new NewItemRequest();
        request.setDescription("Test description");

        ItemRequest result = itemRequestMapper.toItemRequest(request);

        assertEquals("Test description", result.getDescription());
        assertNotNull(result.getCreated());
    }

    @Test
    void toItemRequestDto() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Test description");
        LocalDateTime now = LocalDateTime.now();
        itemRequest.setCreated(now);

        ItemRequestDto resultDto = itemRequestMapper.toItemRequestDto(itemRequest);

        assertEquals(1L, resultDto.getId());
        assertEquals("Test description", resultDto.getDescription());
        assertEquals(now, resultDto.getCreated());
    }

    @Test
    void toItemRequestDtoWithItems() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Test description");
        LocalDateTime now = LocalDateTime.now();
        itemRequest.setCreated(now);

        ItemRequestDtoWithItems result = itemRequestMapper.toItemRequestDtoWithItems(itemRequest);

        assertEquals(1L, result.getId());
        assertEquals("Test description", result.getDescription());
        assertEquals(now, result.getCreated());
        assertNotNull(result.getItems());
        assertTrue(result.getItems().isEmpty());
    }

}