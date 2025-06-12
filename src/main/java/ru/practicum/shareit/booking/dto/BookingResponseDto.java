package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.user.dto.ShortUserResponseDto;

import java.time.LocalDateTime;

@Data
public class BookingResponseDto {

    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemResponseDto item;
    private ShortUserResponseDto booker;
    private BookingStatus status;
}
