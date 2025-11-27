package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

@Component
public class InMemoryUserStorage implements UserStorage {

    private static final Logger log = LoggerFactory.getLogger(InMemoryUserStorage.class);
    private HashMap<Long, User> users = new HashMap<>();
    private static long idCounter = 1;

    public User addUser(final User user){
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
            log.warn("User {} not valid when added", user);
            throw new ValidationException("User no valid ");
        }
    }

    public User updateUser(final User user){
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
                log.warn("User {} not valid when updated", user);
                throw new ValidationException("User no valid");
            }
        } else {
            log.info("User does not have an Id");
            throw new IllegalArgumentException("Id is missing");
        }
    }

    public Collection<User> getAllUsers(){ return users.values(); }

    private static boolean valid(User user) {
        return !user.getEmail().isEmpty() &&
                user.getEmail().contains("@") &&
                !user.getLogin().isEmpty() &&
                !user.getLogin().contains(" ") &&
                user.getBirthday().isBefore(LocalDate.now());
    }

    private static long getIdCounter() {
        return idCounter++;
    }

}
