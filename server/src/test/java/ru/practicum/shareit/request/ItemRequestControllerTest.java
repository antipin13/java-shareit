package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemRequestControllerTest {
    @Mock
    ItemRequestServiceImpl itemRequestService;

    @InjectMocks
    ItemRequestController itemRequestController;

    final ObjectMapper mapper = new ObjectMapper();

    MockMvc mvc;

    ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(itemRequestController)
                .build();
    }

    @Test
    public void createItemRequest() throws Exception {
        Long userId = 1L;

        NewItemRequest request = new NewItemRequest();
        request.setDescription("Test description");

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(100L);
        itemRequestDto.setDescription("Test description");

        when(itemRequestService.saveRequest(eq(userId), any(NewItemRequest.class)))
                .thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.description").value("Test description"));

        verify(itemRequestService, times(1)).saveRequest(eq(userId), any(NewItemRequest.class));
    }

    @Test
    public void findByRequestorId() throws Exception {
        Long userId = 1L;

        ItemRequestDto request1 = new ItemRequestDto();
        request1.setId(101L);
        request1.setDescription("Request 1");

        ItemRequestDto request2 = new ItemRequestDto();
        request2.setId(102L);
        request2.setDescription("Request 2");

        List<ItemRequestDto> requests = Arrays.asList(request1, request2);

        when(itemRequestService.findItemRequestsByRequestorId(userId)).thenReturn(requests);

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(101))
                .andExpect(jsonPath("$[0].description").value("Request 1"))
                .andExpect(jsonPath("$[1].id").value(102))
                .andExpect(jsonPath("$[1].description").value("Request 2"));

        verify(itemRequestService, times(1)).findItemRequestsByRequestorId(userId);
    }

    @Test
    public void returnRequestsFromOtherUsers() throws Exception {
        Long userId = 1L;

        ItemRequestDto itemRequestDto1 = new ItemRequestDto();
        itemRequestDto1.setId(201L);
        itemRequestDto1.setDescription("Request 1");

        ItemRequestDto itemRequestDto2 = new ItemRequestDto();
        itemRequestDto2.setId(202L);
        itemRequestDto2.setDescription("Request 2");

        List<ItemRequestDto> requests = Arrays.asList(itemRequestDto1,itemRequestDto2);

        when(itemRequestService.findItemRequestsOtherUsers(userId)).thenReturn(requests);

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(201))
                .andExpect(jsonPath("$[0].description").value("Request 1"))
                .andExpect(jsonPath("$[1].id").value(202))
                .andExpect(jsonPath("$[1].description").value("Request 2"));

        verify(itemRequestService, times(1)).findItemRequestsOtherUsers(userId);
    }

    @Test
    public void returnItemRequestById() throws Exception {
        Long requestId = 123L;

        ItemRequestDtoWithItems itemRequestDtoWithItems = new ItemRequestDtoWithItems();
        itemRequestDtoWithItems.setId(requestId);
        itemRequestDtoWithItems.setDescription("Test description");

        when(itemRequestService.findById(requestId)).thenReturn(itemRequestDtoWithItems);

        mvc.perform(get("/requests/" + requestId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.description").value("Test description"));

        verify(itemRequestService, times(1)).findById(requestId);
    }

}