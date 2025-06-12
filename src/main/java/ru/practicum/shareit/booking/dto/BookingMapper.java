package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.dto.UserMapper;

public class BookingMapper {

    public static BookingResponseDto toBookingResponseDto(Booking booking) {

        BookingResponseDto dto = new BookingResponseDto();

        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setItem(ItemMapper.toItemResponseDto(booking.getItem()));
        dto.setBooker(UserMapper.toShortUserResponseDto(booking.getBooker()));
        dto.setStatus(booking.getStatus());

        return dto;
    }

}
