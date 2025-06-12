package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

public interface BookingService {

    BookingResponseDto createBooking(BookingCreateDto dto, Long bookerId);

    BookingResponseDto updateBooking(Long bookingId, Long userId, Boolean approved);

    BookingResponseDto getBookingById(Long bookingId);
}
