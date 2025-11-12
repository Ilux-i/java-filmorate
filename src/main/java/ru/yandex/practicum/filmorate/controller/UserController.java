package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

@RestController()
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private static HashMap<Long, User> users = new HashMap<>();
    private static long idCounter = 0;

    @PostMapping
    public User addUser(@RequestBody final User user) {
        if (valid(user)) {
            if (user.getName() == null || user.getName().isEmpty()) {
                user.setName(user.getLogin());
            }
            User newUser = User.builder()
                    .id(getIdCounter())
                    .login(user.getLogin())
                    .email(user.getEmail())
                    .name(user.getName())
                    .birthday(user.getBirthday())
                    .build();
            users.put(newUser.getId(), newUser);
            log.info("User {} added", newUser.getId());
            return newUser;
        } else {
            log.info("User {} not valid when added", user);
            throw new ValidationException("User no valid ");
        }
    }

    @PutMapping
    public User updateUser(@RequestBody final User user) {
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
            if (valid(user)) {
                users.put(user.getId(), user);
                log.info("User {} updated", user.getId());
                return user;
            } else {
                log.info("User {} not valid when updated", user);
                throw new ValidationException("User no valid");
            }
        } else {
            log.info("User does not have an Id");
            throw new IllegalArgumentException("Id is missing");
        }

    }

    @GetMapping
    public Collection<User> getAllUsers() {
        return users.values();
    }

    private boolean valid(User user) {
        return !user.getEmail().isEmpty() &&
                user.getEmail().contains("@") &&
                !user.getLogin().isEmpty() &&
                !user.getLogin().contains(" ") &&
                user.getBirthday().isBefore(LocalDate.now());
    }

    public static long getIdCounter() {
        return idCounter++;
    }
}
