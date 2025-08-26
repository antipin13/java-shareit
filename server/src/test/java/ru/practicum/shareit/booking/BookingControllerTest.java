package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class BookingControllerTest {
    @Mock
    BookingServiceImpl bookingService;

    @InjectMocks
    BookingController bookingController;

    final ObjectMapper mapper = new ObjectMapper();

    MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .build();
    }

    @Test
    public void createBooking() throws Exception {
        Long userId = 1L;

        NewBookingRequest request = new NewBookingRequest();
        request.setItemId(1L);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStatus(BookingStatus.WAITING);

        when(bookingService.saveBooking(eq(userId), any(NewBookingRequest.class)))
                .thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("WAITING"));

        verify(bookingService, times(1)).saveBooking(eq(userId), any(NewBookingRequest.class));
    }

    @Test
    public void approveBooking() throws Exception {
        Long userId = 1L;
        Long bookingId = 10L;
        Boolean approved = true;

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(bookingId);
        bookingDto.setStatus(BookingStatus.APPROVED);

        when(bookingService.approveBooking(bookingId, userId, approved))
                .thenReturn(bookingDto);

        mvc.perform(patch("/bookings/" + bookingId)
                        .header("X-Sharer-User-Id", userId.toString())
                        .param("approved", approved.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(bookingService, times(1)).approveBooking(bookingId, userId, approved);
    }

    @Test
    public void getBookingById() throws Exception {
        Long userId = 1L;
        Long bookingId = 10L;

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(bookingId);
        bookingDto.setStatus(BookingStatus.APPROVED);

        when(bookingService.getBookingByBookerIdOrOwnerId(bookingId, userId))
                .thenReturn(bookingDto);

        mvc.perform(get("/bookings/" + bookingId)
                        .header("X-Sharer-User-Id", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(bookingService, times(1)).getBookingByBookerIdOrOwnerId(bookingId, userId);
    }

    @Test
    public void getUserBookings() throws Exception {
        Long userId = 1L;

        BookingDto booking1 = new BookingDto();
        booking1.setId(101L);
        booking1.setStatus(BookingStatus.WAITING);

        BookingDto booking2 = new BookingDto();
        booking2.setId(102L);
        booking2.setStatus(BookingStatus.APPROVED);

        List<BookingDto> bookings = Arrays.asList(booking1, booking2);

        when(bookingService.getBookingByBooker(userId)).thenReturn(bookings);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(101))
                .andExpect(jsonPath("$[0].status").value("WAITING"))
                .andExpect(jsonPath("$[1].id").value(102))
                .andExpect(jsonPath("$[1].status").value("APPROVED"));

        verify(bookingService, times(1)).getBookingByBooker(userId);
    }
}