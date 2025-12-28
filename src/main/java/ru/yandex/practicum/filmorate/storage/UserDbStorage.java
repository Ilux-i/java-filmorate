package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.repository.FriendsRepository;
import ru.yandex.practicum.filmorate.dao.repository.UserRepository;
import ru.yandex.practicum.filmorate.dto.friend.AllFriendDto;
import ru.yandex.practicum.filmorate.dto.friend.PairFriendDto;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.mapper.FriendMapper.mapToUserPairFriendDto;

@Component("UserDbStorage")
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
    public User getUserById(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User with id " + userId + " not found"));
        user.setFriends(getFriendsByUser(userId));
        return user;
    }

    // Получение списка пользователей по списку userId
    @Override
    public List<User> getUsersByListId(List<Long> usersId) {
        List<User> users = userRepository.findUserByListId(usersId);
        if (users.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> userIds = users.stream()
                .map(User::getId)
                .collect(Collectors.toList());

        List<PairFriendDto> allFriends = friendsRepository
                .findFriendsByListId(userIds)
                .stream()
                .map(dto -> mapToUserPairFriendDto(dto.getUserId(), dto.getFriendId()))
                .toList();

        Map<Long, Set<Long>> friendsByUserId = allFriends.stream()
                .collect(Collectors.groupingBy(
                        PairFriendDto::getUserId,
                        Collectors.mapping(PairFriendDto::getFriendId, Collectors.toSet())
                ));

        users.forEach(user -> {
            Set<Long> friendIds = friendsByUserId.getOrDefault(user.getId(), Collections.emptySet());
            HashMap<Long, FriendshipStatus> friendsMap = friendIds.stream()
                    .collect(Collectors.toMap(
                            friendId -> friendId,
                            friendId -> FriendshipStatus.CONFIRMED,
                            (existing, replacement) -> existing,
                            HashMap::new
                    ));
            user.setFriends(friendsMap);
        });

        return users;
    }

    // Получение всех пользователей
    @Override
    public HashMap<Long, User> getAllUsers() {
        HashMap<Long, User> result = new HashMap<>();
        userRepository.findAll()
                .stream()
                .peek(user -> user.setFriends(getFriendsByUser(user.getId())))
                .forEach(user -> result.put(user.getId(), user));
        return result;
    }

    // Удаление пользователя
    @Override
    public boolean removeUser(User user) {
        return userRepository.remove(user.getId());
    }

    // Добавление в друзья
    @Override
    public long addFriend(AllFriendDto dto) {
        return friendsRepository.addFriend(dto);
    }

    // Подтверждение запроса в друзья
    @Override
    public long confirmedFriend(PairFriendDto dto) {
        return friendsRepository.confirmFriend(dto);
    }

    // Получения друзей
    @Override
    public HashMap<Long, FriendshipStatus> getFriendsByUser(long userId) {
        HashMap<Long, FriendshipStatus> result = new HashMap<>();
        friendsRepository.findFriendsByUserId(userId)
                .forEach(dto -> result.put(dto.getFriendId(), FriendshipStatus.CONFIRMED));
        return result;
    }

    // Получение запросов в друзья
    @Override
    public HashSet<Long> getFriendRequestsByUser(User user) {
        HashSet<Long> result = new HashSet<>();
        friendsRepository.findFriendRequestsByUserId(user.getId())
                .forEach(friendDto -> result.add(friendDto.getFriendId()));
        return result;
    }

    // Удаление друга
    @Override
    public boolean removeFriend(PairFriendDto dto) {
        return friendsRepository.remove(dto);
    }

}