package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserStorage userStorage;

    public User addFriend(long idUser, long idFriend) {
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

    public void removeFriend(long idUser, long idFriend) {

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

    public Collection<User> getFriends(long id) {
        User user = userStorage.getAllUsers().get(id);
        if (user == null) {
            throw new ObjectNotFoundException("Не найден друг с id: " + id);
        }
        return user.getFriends().stream()
                .map(ind -> userStorage.getAllUsers().get(ind))
                .collect(Collectors.toList());
    }

    public Collection<User> getListOfMutualFriends(long userId, long friendId) {
        User user = userStorage.getAllUsers().get(userId);
        User friend = userStorage.getAllUsers().get(friendId);

        if (user == null || friend == null) return Collections.emptyList();

        Set<Long> mutualFriends = new HashSet<>(user.getFriends());
        mutualFriends.retainAll(friend.getFriends());

        return mutualFriends.stream()
                .map(id -> userStorage.getAllUsers().get(id))
                .toList();
    }

}
