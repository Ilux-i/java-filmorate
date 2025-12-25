package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.dto.friend.PairFriendDto;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.Optional;

public interface UserStorage {

    User addUser(final User user);

    User updateUser(final User user);

    Optional<User> getUserById(long userId);

    HashMap<Long, User> getAllUsers();

    // Удаление пользователя
    boolean removeUser(User user);

    long addFriend(PairFriendDto dto);

    // Подтверждение запроса в друзья
    long confirmedFriend(PairFriendDto dto);

    // Получения друзей
    HashMap<Long, FriendshipStatus> getFriendsByUser(long userId);

    // Получение запросов в друзья
    HashMap<Long, FriendshipStatus> getFriendRequestsByUser(User user);

    // Удаление друга
    boolean removeFriend(PairFriendDto dto);
}
