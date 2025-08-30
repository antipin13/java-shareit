package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@FieldDefaults(level = AccessLevel.PRIVATE)
class BookingMapperTest {
    final BookingMapper bookingMapper = new BookingMapper();

    @Test
    public void testToBooking() {
        ItemRepository itemRepository = mock(ItemRepository.class);
        bookingMapper.setItemRepository(itemRepository);

        Item item = new Item();
        item.setId(55L);

        when(itemRepository.findById(55L)).thenReturn(Optional.of(item));

        NewBookingRequest request = new NewBookingRequest();
        request.setItemId(55L);
        request.setStart(LocalDateTime.parse("2023-10-27T12:00"));
        request.setEnd(LocalDateTime.parse("2023-10-28T12:00"));

        Booking booking = bookingMapper.toBooking(request);

        assertEquals(item, booking.getItem());
        assertEquals(LocalDateTime.parse("2023-10-27T12:00"), booking.getStart());
        assertEquals(LocalDateTime.parse("2023-10-28T12:00"), booking.getEnd());
    }

}