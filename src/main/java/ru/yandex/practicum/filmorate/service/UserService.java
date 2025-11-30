package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private final UserStorage userStorage;

    public User addUser(final User user) {
        if (valid(user)) {
            return userStorage.addUser(user);
        } else {
            log.warn("User {} not valid when added", user);
            throw new ValidationException("User no valid ");
        }
    }

    public User updateUser(final User user) {
        HashMap<Long, User> users = userStorage.getAllUsers();
        if (user.getId() != null && users.containsKey(user.getId())) {
            User oldUser = users.get(user.getId());
            if (user.getLogin().isEmpty()) {
                user.setLogin(oldUser.getLogin());
            }
            if (user.getEmail().isEmpty()) {
                user.setEmail(oldUser.getEmail());
            }
            if (user.getName().isEmpty()) {
                user.setName(oldUser.getName());
            }
            if (user.getBirthday() == null) {
                user.setBirthday(oldUser.getBirthday());
            }
            user.setFriends(oldUser.getFriends());
            if (valid(user)) {
                return userStorage.updateUser(user);
            } else {
                log.warn("User {} not valid when updated", user);
                throw new ValidationException("User no valid");
            }
        } else {
            log.info("User does not have an Id");
            throw new ObjectNotFoundException("Id is missing");
        }
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers().values();
    }

    public User addFriend(final long idUser, final long idFriend) {
        User user = userStorage.getAllUsers().get(idUser);
        User friend = userStorage.getAllUsers().get(idFriend);
        if (user != null && friend != null) {
            user.getFriends()
                    .add(idFriend);
            friend.getFriends()
                    .add(user.getId());
            log.info("Пользователи с id: {} и {}, теперь являются друзьями", user, friend);
            return friend;
        } else {
            throw new ObjectNotFoundException("Неверны id");
        }
    }

    public void removeFriend(final long idUser, final long idFriend) {

        User friend = userStorage.getAllUsers().get(idFriend);
        User user = userStorage.getAllUsers().get(idUser);
        if (user != null && friend != null) {
            user.getFriends()
                    .remove(idFriend);
            friend.getFriends()
                    .remove(idUser);
            log.info("Пользователи с id: {} и {}, больше не являются друзьями", user, friend);
        } else {
            throw new ObjectNotFoundException("Неверны id");
        }
    }

    public Collection<User> getFriends(final long id) {
        User user = userStorage.getAllUsers().get(id);
        if (user == null) {
            throw new ObjectNotFoundException("Не найден друг с id: " + id);
        }
        return user.getFriends().stream()
                .map(ind -> userStorage.getAllUsers().get(ind))
                .collect(Collectors.toList());
    }

    public Collection<User> getListOfMutualFriends(final long userId, final long friendId) {
        User user = userStorage.getAllUsers().get(userId);
        User friend = userStorage.getAllUsers().get(friendId);

        if (user == null || friend == null) return Collections.emptyList();

        Set<Long> mutualFriends = new HashSet<>(user.getFriends());
        mutualFriends.retainAll(friend.getFriends());

        return mutualFriends.stream()
                .map(id -> userStorage.getAllUsers().get(id))
                .toList();
    }

    private static boolean valid(User user) {
        return !user.getEmail().isEmpty() &&
                user.getEmail().contains("@") &&
                !user.getLogin().isEmpty() &&
                !user.getLogin().contains(" ") &&
                user.getBirthday().isBefore(LocalDate.now());
    }

}
