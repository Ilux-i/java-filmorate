package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@RestController()
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserStorage userStorage;
    @Autowired
    private UserService userService;

    @PostMapping
    public User addUser(@RequestBody final User user) {
        return userStorage.addUser(user);
    }

    @PutMapping
    public User updateUser(@RequestBody final User user) {
        return userStorage.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriends(@PathVariable final long id, @PathVariable final long friendId) {
        return userService.addFriend(id, friendId);
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers().values();
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable final long id) {
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getMutualFriends(@PathVariable final long id, @PathVariable final long otherId) {
        return userService.getListOfMutualFriends(id, otherId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable long id, @PathVariable long friendId) {
        userService.removeFriend(id, friendId);
    }


}
