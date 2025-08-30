package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.item.dto.*;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemControllerTest {
    @Mock
    ItemServiceImpl itemService;

    @InjectMocks
    ItemController itemController;

    final ObjectMapper mapper = new ObjectMapper();

    MockMvc mvc;

    ItemDto itemDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();
    }

    @Test
    public void createItem() throws Exception {
        Long userId = 1L;
        NewItemRequest request = new NewItemRequest();
        request.setName("Item name");
        request.setDescription("Item description");
        request.setAvailable(true);

        ItemDto responseDto = new ItemDto();
        responseDto.setId(10L);
        responseDto.setName("Item name");
        responseDto.setDescription("Item description");
        responseDto.setAvailable(true);

        when(itemService.saveItem(eq(userId), any(NewItemRequest.class))).thenReturn(responseDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Item name"));

        verify(itemService, times(1)).saveItem(eq(userId), any(NewItemRequest.class));
    }

    @Test
    public void updateItem() throws Exception {
        Long userId = 1L;
        Long itemId = 100L;

        UpdateItemRequest request = new UpdateItemRequest();
        request.setName("Updated name");
        request.setDescription("Updated description");
        request.setAvailable(false);

        ItemDto responseDto = new ItemDto();
        responseDto.setId(itemId);
        responseDto.setName("Updated name");
        responseDto.setDescription("Updated description");
        responseDto.setAvailable(false);

        when(itemService.updateItem(eq(userId), any(UpdateItemRequest.class), eq(itemId))).thenReturn(responseDto);

        mvc.perform(patch("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Updated name"))
                .andExpect(jsonPath("$.description").value("Updated description"))
                .andExpect(jsonPath("$.available").value(false));

        verify(itemService, times(1)).updateItem(eq(userId), any(UpdateItemRequest.class),
                eq(itemId));
    }

    @Test
    public void findItemById() throws Exception {
        Long userId = 1L;
        Long itemId = 100L;

        ItemWithCommentsDto itemDto = new ItemWithCommentsDto();
        itemDto.setId(itemId);
        itemDto.setName("Item name");
        itemDto.setDescription("Test description");
        itemDto.setAvailable(true);

        when(itemService.findItemById(eq(userId), eq(itemId))).thenReturn(itemDto);
        mvc.perform(get("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Item name"))
                .andExpect(jsonPath("$.description").value("Test description"))
                .andExpect(jsonPath("$.available").value(true));

        verify(itemService, times(1)).findItemById(eq(userId), eq(itemId));
    }

    @Test
    public void findItemsByUserId() throws Exception {
        Long userId = 1L;

        ItemWithDateDto item1 = new ItemWithDateDto();
        item1.setId(1L);
        item1.setName("Item 1");

        ItemWithDateDto item2 = new ItemWithDateDto();
        item2.setId(2L);
        item2.setName("Item 2");

        List<ItemWithDateDto> items = Arrays.asList(item1, item2);

        when(itemService.findItemsByUserId(userId)).thenReturn(items);

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Item 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Item 2"));

        verify(itemService, times(1)).findItemsByUserId(userId);
    }

    @Test
    public void searchItems() throws Exception {
        Long userId = 1L;
        String searchText = "item";

        ItemDto item1 = new ItemDto();
        item1.setId(1L);
        item1.setName("Item 1");

        ItemDto item2 = new ItemDto();
        item2.setId(2L);
        item2.setName("Item 2");

        List<ItemDto> resultList = Arrays.asList(item1, item2);

        when(itemService.findItemsByTextAndAvailable(userId, searchText)).thenReturn(resultList);

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId.toString())
                        .param("text", searchText))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Item 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Item 2"));

        verify(itemService, times(1)).findItemsByTextAndAvailable(userId, searchText);
    }

    @Test
    public void createComment() throws Exception {
        Long itemId = 123L;
        Long userId = 456L;

        NewCommentRequest request = new NewCommentRequest();
        request.setText("Test comment");

        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Test comment");

        when(itemService.saveComment(eq(itemId), eq(userId), any(NewCommentRequest.class)))
                .thenReturn(commentDto);

        mvc.perform(post("/items/{id}/comment", itemId)
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("Test comment"));

        verify(itemService, times(1)).saveComment(eq(itemId), eq(userId),
                any(NewCommentRequest.class));
    }
}
