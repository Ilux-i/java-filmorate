package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.mapper.FriendMapper.mapToUserPairFriendDto;
import static ru.yandex.practicum.filmorate.mapper.UserMapper.mapToUpdateUserRequest;
import static ru.yandex.practicum.filmorate.mapper.UserMapper.updateUserFields;

// Нет удаление пользователя
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
        if (user.getId() != null) {
            User oldUser = userStorage.getUserById(user.getId())
                    .orElseThrow(() -> new ObjectNotFoundException("User with id " + user.getId() + " not found"));
            UpdateUserRequest updateUser = mapToUpdateUserRequest(user);
            User result = updateUserFields(oldUser, updateUser);
            if (valid(result)) {
                if(!result.getFriends().isEmpty()) {
                    updateFriends(result.getId(), result.getFriends());
                }
                return userStorage.updateUser(result);
            } else {
                log.warn("User {} not valid when updated", result);
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


    public long addFriend(final long idUser, final long idFriend) {
        userStorage.getUserById(idUser)
                .orElseThrow(() -> new ObjectNotFoundException("User with id " + idUser + " not found"));
        userStorage.getUserById(idFriend)
                .orElseThrow(() -> new ObjectNotFoundException("User with id " + idFriend + " not found"));
        long id = userStorage.addFriend(mapToUserPairFriendDto(idUser, idFriend));
        log.info("Пользователи с id: {} отправил запрос на друзья: {}", idUser, idFriend);
        return id;
    }

    public void removeFriend(final long idUser, final long idFriend) {
        if (userStorage.removeFriend(mapToUserPairFriendDto(idUser, idFriend))) {

            log.info("Пользователи с id: {} и {}, больше не являются друзьями", idUser, idFriend);
        } else {
            throw new ObjectNotFoundException("Неверны id");
        }
    }

    public Collection<User> getFriends(final long id) {
        Set<Long> friendIds = userStorage.getFriendsByUser(id).keySet();
        return friendIds.stream()
                .map(friendId -> userStorage.getUserById(friendId)
                        .orElseThrow(() -> new ObjectNotFoundException("Не найден друг с id: " + friendId)))
                .toList();
    }

    private void updateFriends(long userId, Map<Long, FriendshipStatus> friends) {
        Set<Long> oldFriends = userStorage.getFriendsByUser(userId).keySet();

        Set<Long> toRemove = oldFriends.stream()
                .filter(friend -> !friends.containsKey(friend))
                .collect(Collectors.toSet());

        Set<Long> toAdd = friends.keySet().stream()
                .filter(genre -> !oldFriends.contains(genre))
                .collect(Collectors.toSet());

        toRemove.forEach(friend -> userStorage.removeFriend(
                mapToUserPairFriendDto(userId, friend)));
        toAdd.forEach(friend -> userStorage.addFriend(
                mapToUserPairFriendDto(userId, friend)));
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
