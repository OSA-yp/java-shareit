package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dal.UserStorage;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateRequestDto;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;


    @Override
    public UserResponseDto createUser(UserRequestDto newUser) {

        checkEmailForExisting(-1L, newUser.getEmail()); // проверка, а нет ли уже такой почты

        return UserMapper.toUserResponseDto(
                userStorage.addUser(UserMapper.toUser(newUser)));
    }

    @Override
    public UserResponseDto getUserById(Long userId) {
        User user = checkAndGetUserById(userId);


        return UserMapper.toUserResponseDto(user);
    }

    @Override
    public UserResponseDto updateUser(UserUpdateRequestDto userDataToUpdate, Long userId) {

        checkAndGetUserById(userId);
        checkEmailForExisting(userId, userDataToUpdate.getEmail());

        User user = UserMapper.toUser(userDataToUpdate, userId);

        userStorage.updateUser(user);

        return UserMapper.toUserResponseDto(user);
    }

    @Override
    public void deleteUser(Long userId) {
        userStorage.deleteUser(userId);
    }

    private User checkAndGetUserById(Long id) {

        Optional<User> mayBeUser = userStorage.getUserById(id);

        if (mayBeUser.isPresent()) {
            return mayBeUser.get();
        } else {
            String message = "User with id=" + id + " not found";
            log.warn(message);
            throw new NotFoundException(message);
        }
    }

    private void checkEmailForExisting(Long userId, String email) {
        Optional<User> userToCheck = userStorage.getUserByEmail(email);

        if (userToCheck.isPresent()) {
            if (!userToCheck.get().getId().equals(userId)) {
                String message = email + " is already exist";
                log.warn(message);
                throw new ConflictException(message);
            }
        }
    }
}
