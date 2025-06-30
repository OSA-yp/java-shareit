package ru.practicum.shareit.server.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.server.booking.dto.BookingCreateDto;
import ru.practicum.shareit.server.booking.dto.BookingResponseDto;
import ru.practicum.shareit.server.booking.model.BookingStatus;
import ru.practicum.shareit.server.booking.service.BookingService;
import ru.practicum.shareit.server.item.dto.ItemResponseDto;
import ru.practicum.shareit.server.user.dto.ShortUserResponseDto;
import ru.practicum.shareit.server.user.dto.UserResponseDto;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = BookingController.class)
@Import(BookingControllerTest.TestConfig.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public BookingService bookingService() {
            return Mockito.mock(BookingService.class);
        }
    }

    @BeforeEach
    void setUp(WebApplicationContext wac) throws Exception {

        UserResponseDto savedUser;
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();

        savedUser = new UserResponseDto();
        savedUser.setId(1L);
        savedUser.setName("John Doe");
        savedUser.setEmail("john.doe@example.com");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savedUser)))
                .andExpect(status().isOk());
    }

    @Test
    void createBooking_shouldReturnBookingResponseDto() throws Exception {
        Long userId = 1L;

        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(100L);
        bookingCreateDto.setStart(LocalDateTime.now().plusDays(1));
        bookingCreateDto.setEnd(LocalDateTime.now().plusDays(2));

        BookingResponseDto responseDto = new BookingResponseDto();
        responseDto.setId(10L);
        responseDto.setStart(bookingCreateDto.getStart());
        responseDto.setEnd(bookingCreateDto.getEnd());
        responseDto.setStatus(BookingStatus.WAITING);
        responseDto.setItem(new ItemResponseDto());
        responseDto.setBooker(new ShortUserResponseDto());

        when(bookingService.createBooking(any(), eq(userId)))
                .thenReturn(responseDto);
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingCreateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.status").value(responseDto.getStatus().toString()));
    }

    @Test
    void updateBooking_shouldReturnUpdatedBooking() throws Exception {
        Long userId = 1L;
        Long bookingId = 10L;
        Boolean approved = true;

        BookingResponseDto responseDto = new BookingResponseDto();
        responseDto.setId(bookingId);
        responseDto.setStatus(BookingStatus.APPROVED);

        when(bookingService.updateBooking(bookingId, userId, approved))
                .thenReturn(responseDto);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", approved.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.status").value("APPROVED"));


    }
}
