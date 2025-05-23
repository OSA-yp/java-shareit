package ru.practicum.shareit.user.dal;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Optional;

@Repository
public class InMemoryUserStorage implements UserStorage {

    private final HashMap<Long, User> users = new HashMap<>();
    private Long usersId = 0L;


    public Optional<User> getUserById(Long userId) {
        if (users.containsKey(userId)) {
            return Optional.of(users.get(userId));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    public User addUser(User newUser) {
        usersId++;
        newUser.setId(usersId);

        users.put(usersId, newUser);

        return newUser;
    }

    public User updateUser(User userToUpdate) {
        User user = users.get(userToUpdate.getId());

        user.setName(userToUpdate.getName());
        user.setEmail(userToUpdate.getEmail());

        return user;
    }

    public void deleteUser(Long userId) {
        users.remove(userId);
    }

}
