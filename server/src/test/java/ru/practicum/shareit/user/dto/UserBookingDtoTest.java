package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserBookingDtoTest {
    @Test
    public void userBookingDtoBuilder() {
        UserBookingDto userBooking = new UserBookingDto();
        userBooking.setId(1L);

        assertNotNull(userBooking);
        assertEquals(1L, userBooking.getId());
    }

}