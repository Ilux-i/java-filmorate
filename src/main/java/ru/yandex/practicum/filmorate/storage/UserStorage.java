package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.dto.friend.AllFriendDto;
import ru.yandex.practicum.filmorate.dto.friend.PairFriendDto;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public interface UserStorage {

    User addUser(final User user);

    User updateUser(final User user);

    User getUserById(long userId);

    // Получение списка пользоватеелеей по списку userId
    List<User> getUsersByListId(List<Long> usersId);

    HashMap<Long, User> getAllUsers();

    // Удаление пользователя
    boolean removeUser(User user);

    long addFriend(AllFriendDto dto);

    // Подтверждение запроса в друзья
    long confirmedFriend(PairFriendDto dto);

    // Получения друзей
    HashMap<Long, FriendshipStatus> getFriendsByUser(long userId);

    // Получение запросов в друзья
    HashSet<Long> getFriendRequestsByUser(User user);

    // Удаление друга
    boolean removeFriend(PairFriendDto dto);
}
