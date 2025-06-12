package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.Collection;

public interface BookingService {

    BookingResponseDto createBooking(BookingCreateDto dto, Long bookerId);

    BookingResponseDto updateBooking(Long bookingId, Long userId, Boolean approved);

    BookingResponseDto getBookingById(Long bookingIdm, Long userId);

    Collection<BookingResponseDto> getAllBookingAtState(Long userId, String state);

    Collection<BookingResponseDto> getAllOwnerBookingAtState(Long userId, String state);
}
