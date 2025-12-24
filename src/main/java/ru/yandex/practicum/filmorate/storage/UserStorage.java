package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.dto.PairFriendDto;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.Optional;

public interface UserStorage {

    User addUser(final User user);

    User updateUser(final User user);

    Optional<User> getUserById(long userId);

    HashMap<Long, User> getAllUsers();

    long addFriend(PairFriendDto dto);

    // Подтверждение запроса в друзья
    long confirmedFriend(PairFriendDto dto);

    // Получения друзей
    HashMap<Long, FriendshipStatus> getFriendsByUser(User user);

    // Получение запросов в друзья
    HashMap<Long, FriendshipStatus> getFriendRequestsByUser(User user);
}
