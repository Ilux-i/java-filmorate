package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.dto.friend.AllFriendDto;
import ru.yandex.practicum.filmorate.dto.friend.PairFriendDto;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;

public interface UserStorage {

    // Добавление пользователя
    User addUser(final User user);

    // Обновление пользователя
    User updateUser(final User user);

    // Удаление пользователя
    void deleteUser(long id);

    // получение пользователя по id
    User getUserById(long userId);

    // Получение списка пользоватеелеей по списку userId
    List<User> getUsersByListId(List<Long> usersId);

    HashMap<Long, User> getAllUsers();

    long addFriend(AllFriendDto dto);

    // Получения друзей
    HashMap<Long, FriendshipStatus> getFriendsByUser(long userId);

    // Удаление друга
    boolean removeFriend(PairFriendDto dto);

    // Проверка наличия пользователя с таким id
    boolean contains(long id);
}
