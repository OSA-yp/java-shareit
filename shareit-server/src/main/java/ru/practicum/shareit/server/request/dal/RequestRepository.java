package ru.practicum.shareit.server.request.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.request.dto.RequestWithItemsResponseDto;
import ru.practicum.shareit.server.request.model.Request;

import java.util.Collection;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Collection<Request> findAllByRequestorOrderByCreated(Long requestor);

    Collection<Item> findAllByRequestorIn(Collection<Long> requestors);

    Collection<Request> findAllByRequestorIsNot(Long requestor);

    Optional<Request> getRequestById(Long id);
}
