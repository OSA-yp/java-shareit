package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingController {

    private final BookingService service;

    @PostMapping
    BookingResponseDto createBooking(@RequestBody
                                     @Valid
                                     BookingCreateDto newBookingData,
                                     @RequestHeader("X-Sharer-User-Id")
                                     Long userId) {
        return service.createBooking(newBookingData, userId);
    }


    @PatchMapping("{bookingId}")
    BookingResponseDto updateBooking(@RequestHeader("X-Sharer-User-Id")
                                     Long userId,
                                     @PathVariable Long bookingId,
                                     @RequestParam Boolean approved) {
        return service.updateBooking(bookingId, userId, approved);
    }



// TODO GET /bookings/{bookingId}
// Может быть выполнено либо автором бронирования, либо владельцем вещи, к которой относится бронирование

// TODO GET /bookings?state={state}
// Параметр state необязательный и по умолчанию равен ALL (англ. «все»).
//
// Также он может принимать значения
// CURRENT (англ. «текущие»),
// PAST (англ. «завершённые»),
// FUTURE (англ. «будущие»),
// WAITING (англ. «ожидающие подтверждения»),
// REJECTED (англ. «отклонённые»).
//
// Бронирования должны возвращаться отсортированными по дате от более новых к более старым.

}
