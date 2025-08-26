package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findByRequestorIdOrderByCreatedDesc(Long requestorId);

    List<ItemRequest> findByRequestorIdNotOrderByCreatedDesc(Long requestorId);

    Optional<ItemRequest> findById(Long id);
}
