package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.ShortUserResponseDto;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {

    public static BookingResponseDto toBookingResponseDto(Booking booking,
                                                          ItemResponseDto itemDto,
                                                          ShortUserResponseDto userDto) {

        BookingResponseDto dto = new BookingResponseDto();

        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setItem(itemDto);
        dto.setBooker(userDto);
        dto.setStatus(booking.getStatus());

        return dto;
    }

    public static Booking toBooking(User booker, Item item, BookingCreateDto dto, BookingStatus status) {

        Booking newBooking = new Booking();

        newBooking.setBooker(booker);
        newBooking.setItem(item);
        newBooking.setStart(dto.getStart());
        newBooking.setEnd(dto.getEnd());
        newBooking.setStatus(status);

        return newBooking;
    }
}
