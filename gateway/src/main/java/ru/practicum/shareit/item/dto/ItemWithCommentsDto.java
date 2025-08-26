package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class ItemWithCommentsDto {
    Long id;
    String name;
    String description;
    Boolean available;
    Long ownerId;
    Long requestId;
    LocalDateTime lastBooking;
    LocalDateTime nextBooking;
    List<CommentDto> comments = new ArrayList<>();
}
