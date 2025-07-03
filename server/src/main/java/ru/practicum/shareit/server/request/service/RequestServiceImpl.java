package ru.practicum.shareit.server.request.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.item.dal.ItemRepository;
import ru.practicum.shareit.server.item.dto.ItemInRequestResponseDto;
import ru.practicum.shareit.server.item.dto.ItemMapper;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.request.dal.RequestRepository;
import ru.practicum.shareit.server.request.dto.RequestMapper;
import ru.practicum.shareit.server.request.dto.RequestRequestDto;
import ru.practicum.shareit.server.request.dto.RequestResponseDto;
import ru.practicum.shareit.server.request.dto.RequestWithItemsResponseDto;
import ru.practicum.shareit.server.request.model.Request;
import ru.practicum.shareit.server.user.dal.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public RequestResponseDto createRequest(RequestRequestDto newRequestDto, Long creatorId) {

        // проверка пользователя
        checkUser(creatorId);

        Request newRequest = RequestMapper.toRequest(newRequestDto, creatorId);

        return RequestMapper.toRequestResponseDto(requestRepository.save(newRequest));
    }


    @Override
    public Collection<RequestWithItemsResponseDto> getUserRequests(Long userId) {

        // проверка пользователя
        checkUser(userId);

        // получаем все запросы пользователя
        Collection<Request> requests = requestRepository.findAllByRequestorOrderByCreated(userId);

        return getRequestWithItemsResponseDtos(requests);
    }


    @Override
    public Collection<RequestWithItemsResponseDto> getOtherUsersRequests(Long userId) {

        // проверка пользователя
        checkUser(userId);

        // получаем все запросы пользователей кроме того, кто запросил
        Collection<Request> requests = requestRepository.findAllByRequestorIsNot(userId);

        return getRequestWithItemsResponseDtos(requests);
    }

    @Override
    public RequestWithItemsResponseDto getRequestById(Long requestId) {

        // получаем запрос
        Optional<Request> maybeRequest = requestRepository.getRequestById(requestId);

        // проверка запроса
        if (maybeRequest.isEmpty()) {
            throw new NotFoundException("Request with id=" + requestId + " not found");
        }

        // финальный результат с получением вещей по запросу
        return RequestMapper.toRequestWithItemsResponseDto(maybeRequest.get(),
                itemRepository.findAllByRequestIn(Set.of(requestId)).stream()
                        .map(ItemMapper::toItemInRequestResponseDto)
                        .collect(Collectors.toSet()));

    }

    private void checkUser(Long userId) {

        if (userRepository.getUserById(userId).isEmpty()) {
            throw new NotFoundException("User with id=" + userId + " not found");
        }
    }

    private Collection<RequestWithItemsResponseDto> getRequestWithItemsResponseDtos(Collection<Request> requests) {

        // Для каждого запроса указаны описание, дата и время создания,
        // а также список ответов в формате:
        // id вещи, название, id владельца.
        //
        // В дальнейшем, используя указанные id вещей, можно будет получить подробную информацию о каждой из них.
        // Запросы должны возвращаться отсортированными от более новых к более старым.


        // формируем список id запросов
        Collection<Long> requestsIds = requests.stream()
                .map(Request::getId)
                .collect(Collectors.toSet());

        //получаем все вещи с id всех запросов
        Collection<Item> items = itemRepository.findAllByRequestIn(requestsIds);


        // собираем мапу запрос - вещи
        Map<Long, List<Item>> requestsItems = items.stream()
                .collect(Collectors.groupingBy(Item::getRequest));

        // формируем результат
        Collection<RequestWithItemsResponseDto> result = requests.stream()
                .map(request -> {

                    // подбираем набор вещей
                    Collection<Item> requestItems = requestsItems.get(request.getId());

                    // переводим вещи в dto
                    Collection<ItemInRequestResponseDto> itemsDto = new HashSet<>();

                    if (requestItems != null){
                        itemsDto =
                                requestItems.stream()
                                        .map(ItemMapper::toItemInRequestResponseDto)
                                        .collect(Collectors.toSet());
                    }

                    // формируем финальный dto
                    return RequestMapper.toRequestWithItemsResponseDto(request, itemsDto);

                })
                .collect(Collectors.toSet());
        return result;
    }
}
