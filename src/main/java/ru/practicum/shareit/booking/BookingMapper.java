package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserBookingDto;

import static ru.practicum.shareit.booking.BookingServiceImpl.NOT_FOUND_MESSAGE_ITEM;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingMapper {
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    ItemMapper itemMapper;
    @Autowired
    UserMapper userMapper;

    public Booking toBooking(NewBookingRequest request) {
        Booking booking = new Booking();
        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_MESSAGE_ITEM, request.getItemId())));

        booking.setItem(item);
        booking.setStart(request.getStart());
        booking.setEnd(request.getEnd());

        return booking;
    }

    public BookingDto toBookingDto(Booking booking) {
        ItemBookingDto item = itemMapper.toItemBookingDto(booking.getItem());
        UserBookingDto user = userMapper.toUserBookingDto(booking.getBooker());

        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(item)
                .booker(user)
                .status(booking.getStatus())
                .nameBookingItem(booking.getItem().getName())
                .build();
    }
}
