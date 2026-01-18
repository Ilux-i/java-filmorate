package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@Slf4j
@RestController()
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Добавления пользователя
    @PostMapping
    public User addUser(@RequestBody final User user) {
        return userService.addUser(user);
    }

    // Обновление пользователя
    @PutMapping
    public User updateUser(@RequestBody final User user) {
        return userService.updateUser(user);
    }

    // Удаление пользователя
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable long id) {
        userService.remove(id);
    }

    // Добавление в друзья
    @PutMapping("/{id}/friends/{friendId}")
    public User addFriends(@PathVariable final long id, @PathVariable final long friendId) {
        return userService.addFriend(id, friendId);
    }

    // Получение пользователя по его id
    @GetMapping("/{userId}")
    public User getFilm(@PathVariable final long userId) {
        return userService.getUserById(userId);
    }

    // Получение всех пользователей
    @GetMapping
    public Collection<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // Получение всех друзей пользователя
    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable final long id) {
        return userService.getFriends(id);
    }

    // Получение общих друзей двух пользователей
    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getMutualFriends(@PathVariable final long id, @PathVariable final long otherId) {
        return userService.getListOfMutualFriends(id, otherId);
    }

    // Разрыв дружеской связи между двумя пользователями
    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable long id, @PathVariable long friendId) {
        userService.removeFriend(id, friendId);
    }


}
