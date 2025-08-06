package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findById(Long id);

    List<Booking> findByItemId(Long itemId);

    Optional<Booking> findByItemIdAndBookerId(Long itemId, Long bookerId);

    @Query("select b from Booking b where b.id = ?1 and b.item.owner.id = ?2")
    Optional<Booking> findByIdAndOwnerId(Long id, Long ownerId);

    Optional<Booking> findByIdAndBookerId(Long id, Long bookerId);

    List<Booking> findByBookerId(Long bookerId);
}
