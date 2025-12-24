package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.PairFriendDto;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private static HashMap<Long, User> users = new HashMap<>();
    private static long idCounter = 1;

    public User addUser(final User user) {
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
    }

    public User updateUser(final User user) {
        users.put(user.getId(), user);
        log.info("User {} updated", user.getId());
        return user;
    }

    @Override
    public Optional<User> getUserById(long userId) {
        return Optional.empty();
    }

    public HashMap<Long, User> getAllUsers() {
        return users;
    }

    @Override
    public long addFriend(PairFriendDto dto) {
        return 0;
    }

    @Override
    public long confirmedFriend(PairFriendDto dto) {
        return 0;
    }

    @Override
    public HashMap<Long, FriendshipStatus> getFriendsByUser(User user) {
        return null;
    }

    @Override
    public HashMap<Long, FriendshipStatus> getFriendRequestsByUser(User user) {
        return null;
    }

    private static long getIdCounter() {
        return idCounter++;
    }

}
