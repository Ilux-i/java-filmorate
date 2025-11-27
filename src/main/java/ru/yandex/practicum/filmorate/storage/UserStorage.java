package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    User addUser(final User user);

    User updateUser(final User user);

    Collection<User> getAllUsers();

}
