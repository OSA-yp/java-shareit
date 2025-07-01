package ru.practicum.shareit.server.booking.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.server.booking.model.Booking;
import ru.practicum.shareit.server.booking.model.BookingStatus;
import ru.practicum.shareit.server.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Все бронирования пользователя, отсортированные по дате, с определенным статусом
    Collection<Booking> findBookingsByBookerAndStatusOrderByStartDesc(User booker, BookingStatus status);


    // Все бронирования пользователя, отсортированные по дате
    Collection<Booking> findBookingsByBookerOrderByStartDesc(User booker);

    // Текущие бронирования: APPROVED и start <= now <= end
    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker = :user AND b.status = 'APPROVED' " +
            "AND b.start <= CURRENT_TIMESTAMP AND b.end >= CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    Collection<Booking> findCurrentBookings(@Param("user") User user);

    // Прошедшие бронирования: APPROVED и end < now
    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker = :user AND b.status = 'APPROVED' " +
            "AND b.end < CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    Collection<Booking> findPastBookings(@Param("user") User user);

    // Будущие бронирования: APPROVED и start > now
    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker = :user AND b.status = 'APPROVED' " +
            "AND b.start > CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    Collection<Booking> findFutureBookings(@Param("user") User user);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner = :ownerId AND b.status = 'APPROVED' " +
            "AND b.start <= CURRENT_TIMESTAMP AND b.end >= CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    Collection<Booking> findCurrentBookingsByOwner(@Param("ownerId") Long ownerId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner = :ownerId AND b.status = 'APPROVED' " +
            "AND b.end < CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    Collection<Booking> findPastBookingsByOwner(@Param("ownerId") Long ownerId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner = :ownerId AND b.status = 'APPROVED' " +
            "AND b.start > CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    Collection<Booking> findFutureBookingsByOwner(@Param("ownerId") Long ownerId);

    // Все бронирования владельца, отсортированные по дате, с определенным статусом
    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner = :ownerId AND b.status = :status " +
            "ORDER BY b.start DESC")
    Collection<Booking> findBookingsByOwnerAndStatus(@Param("ownerId") Long ownerId, @Param("status") BookingStatus status);

    // Все бронирования владельца, отсортированные по дате
    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner = :ownerId " +
            "ORDER BY b.start DESC")
    Collection<Booking> findAllBookingsByOwner(@Param("ownerId") Long ownerId);

    Optional<Booking> findTopByItemIdAndStartAfterOrderByStartAsc(Long id, LocalDateTime now);

    Optional<Booking> findTopByItemIdAndEndBeforeOrderByEndDesc(Long id, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.id IN :itemIds AND b.status = 'APPROVED' ORDER BY b.start DESC")
    List<Booking> findApprovedBookingsByItemIdsOrderByDesc(@Param("itemIds") List<Long> itemIds);
}
