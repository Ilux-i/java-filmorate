package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.repository.FriendsRepository;
import ru.yandex.practicum.filmorate.dao.repository.UserRepository;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.mapper.FriendMapper.mapToAllFriendDto;
import static ru.yandex.practicum.filmorate.mapper.FriendMapper.mapToUserPairFriendDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private final UserStorage userStorage;
    @Autowired
    private FriendsRepository friendsRepository;

    // Добавление пользователя
    public User addUser(final User user) {
        // Проверка валидации
        if (valid(user)) {
            // Добавление
            User result = userStorage.addUser(user);
            if (result != null) {
                log.info("User added: {}", result);
            } else {
                log.info("User added but not saved");
            }
            return result;
        } else {
            log.warn("User {} not valid when added", user);
            throw new ValidationException("User no valid ");
        }
    }

    // Обновление пользователя
    public User updateUser(final User user) {
        if (user.getId() != null) {
            // Старые данные пользователя
            User oldUser = userStorage.getUserById(user.getId());
            // Новые данные пользователя
            UpdateUserRequest updateUser = UserMapper.mapToUpdateUserRequest(user);
            // Обновлённые данные пользователя
            User result = UserMapper.updateUserFields(oldUser, updateUser);
            // Проверка валидации
            if (valid(result)) {
                if (!result.getFriends().isEmpty()) {
                    updateFriends(result.getId(), result.getFriends());
                }
                return userStorage.updateUser(result);
            } else {
                log.warn("User {} not valid when updated", result);
                throw new ValidationException("User no valid");
            }
        } else {
            log.info("User does not have an Id");
            throw new ObjectNotFoundException("Id is missing");
        }
    }

    // Получение всех пользователей
    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers().values();
    }

    // Добавление друга
    public User addFriend(final long idUser, final long idFriend) {
        User user = userStorage.getUserById(idUser);
        userStorage.getUserById(idFriend);
        long id = userStorage.addFriend(mapToAllFriendDto(idUser, idFriend));
        log.info("Пользователи с id: {} отправил запрос на друзья: {}", idUser, idFriend);
        return user;
    }

    // Удаление друга
    public void removeFriend(final long idUser, final long idFriend) {
        User user = userStorage.getUserById(idUser);
        userStorage.getUserById(idFriend);
        if (userStorage.removeFriend(mapToUserPairFriendDto(idUser, idFriend))) {
            log.info("Пользователи с id: {} и {}, больше не являются друзьями", idUser, idFriend);
        }
    }

    // Получение списка друзей
    public Collection<User> getFriends(final long id) {
        userStorage.getUserById(id);
        List<Long> friendIds = userStorage.getFriendsByUser(id).keySet().stream().toList();
        return userStorage.getUsersByListId(friendIds);
    }

    // Обновление списка друзей
    private void updateFriends(long userId, HashMap<Long, FriendshipStatus> friends) {
        // Старый список друзей
        Set<Long> oldFriends = userStorage.getFriendsByUser(userId).keySet();

        // Список друзей на удаление
        Set<Long> toRemove = oldFriends.stream()
                .filter(friend -> !friends.containsKey(friend))
                .collect(Collectors.toSet());

        // Список друзей на добавление
        Set<Long> toAdd = friends.keySet().stream()
                .filter(genre -> !oldFriends.contains(genre))
                .collect(Collectors.toSet());

        friendsRepository.removeFriendsByListId(toRemove.stream().map(friend -> mapToUserPairFriendDto(userId, friend)).toList());
        friendsRepository.addFriendsByListId(toAdd.stream().map(friend -> mapToUserPairFriendDto(userId, friend)).toList());
    }

//    // Подтверждение запроса добавления в друзья
//    public long confirmedFriend(final long idUser, final long idFriend) {
//        return userStorage.confirmedFriend(mapToUserPairFriendDto(idUser, idFriend));
//    }

    private static boolean valid(User user) {
        return !user.getEmail().isEmpty() &&
                user.getEmail().contains("@") &&
                !user.getLogin().isEmpty() &&
                !user.getLogin().contains(" ") &&
                user.getBirthday().isBefore(LocalDate.now());
    }

    // Получение списка общих друзей между двумя пользователями
    public Collection<User> getListOfMutualFriends(long id, long otherId) {
        userStorage.getUserById(id);
        userStorage.getUserById(otherId);
        Set<Long> friends = userStorage.getFriendsByUser(id).keySet();
        return userStorage.getUsersByListId(
                userStorage.getFriendsByUser(otherId).keySet()
                        .stream()
                        .filter(friends::contains)
                        .toList());
    }
}
