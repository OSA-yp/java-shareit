package ru.practicum.shareit.server.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.server.booking.dal.BookingRepository;
import ru.practicum.shareit.server.booking.dto.BookingCreateDto;
import ru.practicum.shareit.server.booking.dto.BookingMapper;
import ru.practicum.shareit.server.booking.dto.BookingResponseDto;
import ru.practicum.shareit.server.booking.dto.BookingStatusDto;
import ru.practicum.shareit.server.booking.model.Booking;
import ru.practicum.shareit.server.booking.model.BookingStatus;
import ru.practicum.shareit.server.exception.ForbiddenException;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.exception.ValidationException;
import ru.practicum.shareit.server.item.dal.ItemRepository;
import ru.practicum.shareit.server.item.dto.ItemMapper;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.user.dal.UserRepository;
import ru.practicum.shareit.server.user.dto.UserMapper;
import ru.practicum.shareit.server.user.model.User;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

// Логирование ошибок в ErrorResponse, логирование запросов - org.zalando
@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    private static final BookingStatus FIRST_BOOKING_STATUS = BookingStatus.WAITING;

    @Override
    public BookingResponseDto createBooking(BookingCreateDto dto, Long bookerId) {

        User booker = checkAndgetUser(bookerId);

        Item item = checkAndgetItem(dto.getItemId());

        if (!item.getAvailable()) {
            throw new ValidationException("Item with id=" + item.getId() + " not available");
        }

        if (dto.getEnd().equals(dto.getStart()) || dto.getEnd().isBefore(dto.getStart())) {
            throw new ValidationException("End date must be after start date");
        }

        Booking newBooking = BookingMapper.toBooking(booker, item, dto, FIRST_BOOKING_STATUS);


        return BookingMapper.toBookingResponseDto(bookingRepository.save(newBooking),
                ItemMapper.toItemResponseDto(newBooking.getItem()),
                UserMapper.toShortUserResponseDto(newBooking.getBooker()));
    }


    @Override
    public BookingResponseDto updateBooking(Long bookingId, Long bookerId, Boolean approved) {

        User booker = checkAndgetUser(bookerId);
        Booking booking = checkAndGetBooking(bookingId);

        if (!booking.getItem().getOwner().equals(booker.getId())) {
            throw new ForbiddenException("Only item owner can change booking approve");
        }

        if (booking.getStatus().equals(BookingStatus.WAITING)) {
            if (approved) {
                booking.setStatus(BookingStatus.APPROVED);
            } else {
                booking.setStatus(BookingStatus.REJECTED);
            }
        } else {
            throw new ValidationException("Booking can approved or rejected in WAITING status");
        }

        bookingRepository.save(booking);

        return BookingMapper.toBookingResponseDto(booking,
                ItemMapper.toItemResponseDto(booking.getItem()),
                UserMapper.toShortUserResponseDto(booking.getBooker()));
    }

    @Override
    public BookingResponseDto getBookingById(Long bookingId, Long userId) {


        User user = checkAndgetUser(userId);
        Booking booking = checkAndGetBooking(bookingId);

        // Может быть выполнено либо автором бронирования, либо владельцем вещи, к которой относится бронирование
        if (!(user.getId().equals(booking.getBooker().getId()) ||
                user.getId().equals(booking.getItem().getOwner()))) {
            throw new ForbiddenException("Only item owner or booker can get booking");
        }

        return BookingMapper.toBookingResponseDto(booking,
                ItemMapper.toItemResponseDto(booking.getItem()),
                UserMapper.toShortUserResponseDto(booking.getBooker()));
    }

    @Override
    public Collection<BookingResponseDto> getAllBookingAtState(Long userId, String state) {

        BookingStatusDto stateDTO = getBookingStatusDto(state);
        User booker = checkAndgetUser(userId);

        Collection<Booking> bookings = switch (stateDTO) {
            case ALL -> bookingRepository.findBookingsByBookerOrderByStartDesc(booker);
            case CURRENT -> bookingRepository.findCurrentBookings(booker);
            case PAST -> bookingRepository.findPastBookings(booker);
            case FUTURE -> bookingRepository.findFutureBookings(booker);
            default -> bookingRepository.findBookingsByBookerAndStatusOrderByStartDesc(
                    booker, BookingStatus.valueOf(stateDTO.name()));
        };

        return bookings.stream()
                .map(booking -> BookingMapper.toBookingResponseDto(booking,
                        ItemMapper.toItemResponseDto(booking.getItem()),
                        UserMapper.toShortUserResponseDto(booking.getBooker())))
                .collect(Collectors.toSet());

    }


    @Override
    public Collection<BookingResponseDto> getAllOwnerBookingAtState(Long userId, String state) {
        BookingStatusDto stateDTO = getBookingStatusDto(state);
        User booker = checkAndgetUser(userId);

        Collection<Booking> bookings = switch (stateDTO) {
            case ALL -> bookingRepository.findAllBookingsByOwner(booker.getId());
            case CURRENT -> bookingRepository.findCurrentBookingsByOwner(booker.getId());
            case PAST -> bookingRepository.findPastBookingsByOwner(booker.getId());
            case FUTURE -> bookingRepository.findFutureBookingsByOwner(booker.getId());
            default -> bookingRepository.findBookingsByOwnerAndStatus(
                    booker.getId(), BookingStatus.valueOf(stateDTO.name()));
        };

        return bookings.stream()
                .map(booking -> BookingMapper.toBookingResponseDto(booking,
                        ItemMapper.toItemResponseDto(booking.getItem()),
                        UserMapper.toShortUserResponseDto(booking.getBooker())))
                .collect(Collectors.toSet());
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

        return item;
    }

    private Booking checkAndGetBooking(Long id) {

        Optional<Booking> maybeBooking = bookingRepository.findById(id);

        if (maybeBooking.isEmpty()) {
            throw new NotFoundException("Booking with id=" + id + " not found");
        }

        return maybeBooking.get();
    }

    private BookingStatusDto getBookingStatusDto(String state) {
        BookingStatusDto stateDTO;
        try {
            stateDTO = BookingStatusDto.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid state= " + state);
        }
        return stateDTO;
    }
}
