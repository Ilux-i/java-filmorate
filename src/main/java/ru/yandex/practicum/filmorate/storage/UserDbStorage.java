package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.dao.repository.FriendsRepository;
import ru.yandex.practicum.filmorate.dao.repository.UserRepository;
import ru.yandex.practicum.filmorate.dto.PairFriendDto;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.Optional;

@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final UserRepository userRepository;
    private final FriendsRepository friendsRepository;

    // Добавление пользователя
    @Override
    public User addUser(User user) {
        return userRepository.add(user);
    }

    // Обновление Пользователя
    @Override
    public User updateUser(User user) {
        return userRepository.update(user);
    }

    // Получение пользователя
    @Override
    public Optional<User> getUserById(long userId) {
        return userRepository.findById(userId);
    }

    // Получение всех пользователей
    @Override
    public HashMap<Long, User> getAllUsers() {
        HashMap<Long, User> result = new HashMap<>();
        userRepository.findAll()
                .stream()
                .peek(user -> user.setFriends(getFriendsByUser(user)))
                .forEach(user -> result.put(user.getId(), user));
        return result;
    }

    // Добавление в друзья
    @Override
    public long addFriend(PairFriendDto dto) {
        return friendsRepository.addFriend(dto);
    }

    // Подтверждение запроса в друзья
    @Override
    public long confirmedFriend(PairFriendDto dto) {
        return friendsRepository.confirmFriend(dto);
    }

    // Получения друзей
    @Override
    public HashMap<Long, FriendshipStatus> getFriendsByUser(User user) {
        HashMap<Long, FriendshipStatus> result = new HashMap<>();
        friendsRepository.findFriendsByUserId(user.getId())
                .forEach(friendDto -> result.put(friendDto.getId(), friendDto.getStatus()));
        return result;
    }

    // Получение запросов в друзья
    @Override
    public HashMap<Long, FriendshipStatus> getFriendRequestsByUser(User user) {
        HashMap<Long, FriendshipStatus> result = new HashMap<>();
        friendsRepository.findFriendRequestsByUserId(user.getId())
                .forEach(friendDto -> result.put(friendDto.getId(), friendDto.getStatus()));
        return result;
    }

}
