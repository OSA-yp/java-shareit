package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dal.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

// Логирование ошибок в ErrorResponse, логирование запросов - org.zalando
@Slf4j
@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    private final BookingStatus FIRST_BOOKING_STATUS = BookingStatus.WAITING;

    @Override
    public BookingResponseDto createBooking(BookingCreateDto dto, Long bookerId) {

        User booker = checkAndgetUser(bookerId);

        Item item = checkAndgetItem(dto.getItemId());

        if (dto.getEnd().equals(dto.getStart())) {
            throw new ValidationException("End date must be after start date");
        }

        Booking newBooking = new Booking();

        newBooking.setBooker(booker);
        newBooking.setItem(item);
        newBooking.setStart(dto.getStart());
        newBooking.setEnd(dto.getEnd());
        newBooking.setStatus(FIRST_BOOKING_STATUS);

        return BookingMapper.toBookingResponseDto(bookingRepository.save(newBooking));
    }


    @Override
    public BookingResponseDto updateBooking(Long bookingId, Long bookerId, Boolean approved) {

        User booker = checkAndgetUser(bookerId);
        Booking booking = checkAndGetBooking(bookingId);

        if (!booking.getItem().getOwner().equals(booker.getId())){
            throw new ForbiddenException("Only item owner can change booking approve");
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        bookingRepository.save(booking);

        return BookingMapper.toBookingResponseDto(booking);
    }

    @Override
    public BookingResponseDto getBookingById(Long bookingId) {
        return BookingMapper.toBookingResponseDto(checkAndGetBooking(bookingId));
    }


    private User checkAndgetUser(Long userId) {
        User booker;

        Optional<User> maybeUser = userRepository.getUserById(userId);
        if (maybeUser.isEmpty()) {
            throw new ForbiddenException("User with id=" + userId + " not found");
        } else {
            booker = maybeUser.get();
        }
        return booker;
    }

    private Item checkAndgetItem(Long itemId) {
        Item item;

        Optional<Item> maybeItem = itemRepository.getItemById(itemId);
        if (maybeItem.isEmpty()) {
            throw new NotFoundException("Item with id=" + itemId + " not found");
        }
        item = maybeItem.get();

        if (item.getAvailable().equals(false)) {
            throw new ValidationException("Item with id=" + itemId + " not available");
        }
        return item;
    }

    private Booking checkAndGetBooking(Long id) {

        Optional<Booking> maybeBooking = bookingRepository.findById(id);

        if (maybeBooking.isEmpty()) {
            throw new NotFoundException("Booking with id=" + id + " not found");
        }

        return maybeBooking.get();
    }
}
