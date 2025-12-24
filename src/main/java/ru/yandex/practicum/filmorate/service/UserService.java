package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.*;

import static ru.yandex.practicum.filmorate.mapper.FriendMapper.mapToUserPairFriendDto;

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
            userStorage.addFriend(mapToUserPairFriendDto(idUser, idFriend));
            log.info("Пользователи с id: {} отправил запрос на друзья: {}", user, friend);
            return friend;
        } else {
            throw new ObjectNotFoundException("Неверны id");
        }
    }

//    public void removeFriend(final long idUser, final long idFriend) {
//
//        User friend = userStorage.getAllUsers().get(idFriend);
//        User user = userStorage.getAllUsers().get(idUser);
//        if (user != null && friend != null) {
//            user.getFriends()
//                    .remove(idFriend);
//            friend.getFriends()
//                    .remove(idUser);
//            log.info("Пользователи с id: {} и {}, больше не являются друзьями", user, friend);
//        } else {
//            throw new ObjectNotFoundException("Неверны id");
//        }
//    }

    public Collection<User> getFriends(final long id) {
        User user = userStorage.getUserById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Не найден пользователь с id: " + id));
        Set<Long> friendIds = userStorage.getFriendsByUser(user).keySet();
        return friendIds.stream()
                .map(friendId -> userStorage.getUserById(friendId)
                        .orElseThrow(() -> new ObjectNotFoundException("Не найден друг с id: " + friendId)))
                .toList();
    }

    public long confirmedFriend(final long idUser, final long idFriend){
        return userStorage.confirmedFriend(mapToUserPairFriendDto(idUser, idFriend));
    }

    private static boolean valid(User user) {
        return !user.getEmail().isEmpty() &&
                user.getEmail().contains("@") &&
                !user.getLogin().isEmpty() &&
                !user.getLogin().contains(" ") &&
                user.getBirthday().isBefore(LocalDate.now());
    }

}
