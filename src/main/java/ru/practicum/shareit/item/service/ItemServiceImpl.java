package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dal.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dal.CommentRepository;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

// Логирование ошибок в ErrorResponse, логирование запросов - org.zalando
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository repository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    public ItemResponseDto createItem(ItemRequestDto newItemDto, Long ownerId) {

        userService.getUserById(ownerId); // проверка, а существует ли user

        Item newItem = ItemMapper.toItem(newItemDto);
        newItem.setOwner(ownerId);

        return ItemMapper.toItemResponseDto(repository.save(newItem));
    }

    @Override
    public ItemResponseDto updateItem(Long itemId, ItemUpdateRequestDto itemDataToUpdate, Long userId) {

        userService.getUserById(userId); // проверка, а существует ли user

        Item existingItem = checkAndGetItemById(itemId);

        // Редактировать вещь может только её владелец.
        if (!existingItem.getOwner().equals(userId)) {
            throw new ForbiddenException("Only owner can update item id=" + itemId);
        }

        if (itemDataToUpdate.getName() != null) {
            existingItem.setName(itemDataToUpdate.getName());
        }

        if (itemDataToUpdate.getDescription() != null) {
            existingItem.setDescription(itemDataToUpdate.getDescription());
        }

        if (itemDataToUpdate.getAvailable() != null) {
            existingItem.setAvailable(itemDataToUpdate.getAvailable());
        }

        return ItemMapper.toItemResponseDto(repository.save(existingItem));
    }

    //    @Override
//    public ItemWithCommentsResponseDto getItemById(Long itemId) {
//
//        Item item = checkAndGetItemById(itemId);
//        User author;
//
//        Long lastBooking = bookingRepository.findAllByItemAndEndBefore(item, LocalDateTime.now()).getFirst();
//        Long nextBooking = bookingRepository.findAllByItemAndEndAfter(item, LocalDateTime.now()).getFirst();
//        List<CommentResponseDto> comments = commentRepository.getCommentsByItemId(itemId).stream()
//                .map(comment ->
//                        ItemMapper.toCommentResponseDto(comment, userRepository.getUserById(comment.getAuthorId()).get().getName())
//                )
//                .toList();
//
//        return ItemMapper.toItemWithCommentsResponseDto(item, lastBooking, nextBooking, comments);
//    }
    @Override
    public ItemWithCommentsResponseDto getItemById(Long itemId, Long userId) {

        Optional<User> maybeUser = userRepository.getUserById(userId);
        if (maybeUser.isEmpty()) {
            throw new NotFoundException("User with id=" + userId + " not found");
        }
        User user = maybeUser.get();

        // Получаем Item с проверкой существования
        Item item = checkAndGetItemById(itemId);

        LocalDateTime nextBookingDate = null;
        LocalDateTime lastBookingDate = null;

        // Показываем время бронирования только для владельца вещи
        if (user.getId().equals(item.getOwner())) {
            // Получаем последние и следующие бронирования
            Booking lastBooking = getLatestPastBooking(item);
            Booking nextBooking = getUpcomingBooking(item);


            if (lastBooking != null) {
                lastBookingDate = lastBooking.getEnd();
            }
            if (nextBooking != null) {
                nextBookingDate = nextBooking.getStart();
            }
        }

        // Получаем комментарии с авторами
        List<CommentResponseDto> comments = getCommentsWithAuthors(item);

        return ItemMapper.toItemWithCommentsResponseDto(item, lastBookingDate, nextBookingDate, comments);
    }

    private Booking getLatestPastBooking(Item item) {
        return bookingRepository
                .findTopByItemIdAndEndBeforeOrderByEndDesc(item.getId(), LocalDateTime.now())
                .orElse(null);
    }

    private Booking getUpcomingBooking(Item item) {
        return bookingRepository
                .findTopByItemIdAndStartAfterOrderByStartAsc(item.getId(), LocalDateTime.now())
                .orElse(null);
    }

    private List<CommentResponseDto> getCommentsWithAuthors(Item item) {
        return commentRepository
                .findAllByItemId(item.getId())
                .stream()
                .map(comment -> {
                    String authorName = getUserFullName(comment.getAuthorId());
                    return ItemMapper.toCommentResponseDto(comment, authorName);
                })
                .toList();
    }

    private String getUserFullName(Long userId) {
        return userRepository.findById(userId)
                .map(User::getName)
                .orElse("Unknown User");
    }


    @Override
    public Collection<ItemResponseDto> getItemsByUser(Long userId) {

        userService.getUserById(userId);  // проверка, а существует ли user

        return repository.findByOwner(userId).stream()
                .map(ItemMapper::toItemResponseDto)
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<ItemResponseDto> searchItems(String searchString) {

        if (searchString.isEmpty()) {
            return Set.of();
        }

        return repository.searchItems(searchString).stream()
                .map(ItemMapper::toItemResponseDto)
                .collect(Collectors.toSet());
    }

    @Override
    public CommentResponseDto addComment(CommentRequestDto dto, Long itemId, Long userId) {

        User user;
        Optional<User> maybeUser = userRepository.getUserById(userId);
        if (maybeUser.isEmpty()) {
            throw new NotFoundException("User with id=" + userId + " not found");
        } else {
            user = maybeUser.get();
        }
        Item item = checkAndGetItemById(itemId);

        if (bookingRepository.findPastBookings(user).stream()
                .filter(booking -> booking.getItem().getId().equals(itemId))
                .findFirst().isEmpty()) {
            throw new ValidationException("Only user who booked item can add comment");
        }

        Comment comment = new Comment();
        comment.setText(dto.getText());
        comment.setItemId(itemId);
        comment.setAuthorId(userId);
        comment.setCreated(LocalDateTime.now());

        commentRepository.save(comment);


        return ItemMapper.toCommentResponseDto(comment, user.getName());
    }


    private Item checkAndGetItemById(Long itemId) {

        Optional<Item> maybeItem = repository.getItemById(itemId);

        if (maybeItem.isEmpty()) {
            throw new NotFoundException("Item with id=" + itemId + " not found");
        }
        return maybeItem.get();
    }
}
