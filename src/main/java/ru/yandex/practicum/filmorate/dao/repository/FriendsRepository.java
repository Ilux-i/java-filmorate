package ru.yandex.practicum.filmorate.dao.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dao.mappers.FriendRowMapper;
import ru.yandex.practicum.filmorate.dto.friend.AllFriendDto;
import ru.yandex.practicum.filmorate.dto.friend.PairFriendDto;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class FriendsRepository extends BaseRepository<AllFriendDto> {

    private static final String FIND_FRIENDS_BY_ID_QUERY =
            "SELECT * " +
                    "FROM friends " +
                    "WHERE user_id = ?";
    private static final String FIND_FRIEND_REQUESTS_BY_ID_QUERY =
            "SELECT * " +
                    "FROM friends " +
                    "WHERE " +
                    "friend_id = ? " +
                    "AND status = ?";
    private static final String FIND_BY_LIST_ID_TEMPLATE = "SELECT * FROM friends WHERE user_id IN (%s)";
    private static final String INSERT_QUERY = "INSERT INTO friends(user_id, friend_id, status) " +
            "VALUES (?, ?, ?)";
    private static final String CONFIRM_FRIEND_QUERY = "UPDATE friends SET status = " + FriendshipStatus.CONFIRMED +
            " WHERE user_id = ? AND friend_id = ?";
    private static final String REMOVE_FRIEND_QUERY = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
    private static final String DELETE_BY_LIST_QUERY = "DELETE FROM friends WHERE ";

    public FriendsRepository(JdbcTemplate jdbc, FriendRowMapper mapper) {
        super(jdbc, mapper);
    }

    // Получение списка id друзей по id пользователя
    public List<AllFriendDto> findFriendsByUserId(long userId) {
        return findMany(FIND_FRIENDS_BY_ID_QUERY, userId);
    }

//    // Получение входящих запросов в друзья по id пользователя
//    public List<AllFriendDto> findFriendRequestsByUserId(long userId) {
//        return findMany(
//                FIND_FRIEND_REQUESTS_BY_ID_QUERY,
//                userId,
//                FriendshipStatus.CONFIRMED.toString()
//        );
//    }

    //  Получение связей пользователь-друг по списку пользователей
    public List<AllFriendDto> findFriendsByListId(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyList();
        }

        String placeholders = String.join(",",
                Collections.nCopies(userIds.size(), "?"));

        String sql = String.format(
                FIND_BY_LIST_ID_TEMPLATE,
                placeholders
        );

        return findMany(sql, userIds.toArray());
    }

    // Добавление множества связей пользователь-друг
    @Transactional
    public void addFriendsByListId(List<PairFriendDto> pairs) {
        if (pairs == null || pairs.isEmpty()) {
            return;
        }

        String sql = "INSERT INTO friends(user_id, friend_id, status) VALUES (?, ?, ?)";
        String confirmedStatus = FriendshipStatus.CONFIRMED.toString();

        List<Object[]> batchArgs = pairs.stream()
                .map(pair -> new Object[]{
                        pair.getUserId(),
                        pair.getFriendId(),
                        confirmedStatus
                })
                .collect(Collectors.toList());

        jdbc.batchUpdate(sql, batchArgs);
    }

    // Добавление связи пользователь-друг
    public long addFriend(AllFriendDto dto) {
        return insert(
                INSERT_QUERY,
                dto.getUserId(),
                dto.getFriendId(),
                FriendshipStatus.CONFIRMED.toString()
        );
    }

//    // Подтверждение запроса в друзья
//    public long confirmFriend(PairFriendDto dto) {
//        return insert(
//                CONFIRM_FRIEND_QUERY,
//                dto.getUserId(),
//                dto.getFriendId()
//        );
//    }

    // Удаление связи пользователь-друг
    public boolean remove(PairFriendDto dto) {
        return jdbc.update(REMOVE_FRIEND_QUERY, dto.getUserId(), dto.getFriendId()) > 0;
    }

    // Удаление связей пользователь-друг
    public void removeFriendsByListId(List<PairFriendDto> pairs) {
        List<String> conditions = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        for (PairFriendDto pair : pairs) {
            conditions.add("(user_id = ? AND friend_id = ?)");
            params.add(pair.getUserId());
            params.add(pair.getFriendId());
        }
        String whereClause = String.join(" OR ", conditions);

        update(DELETE_BY_LIST_QUERY + whereClause, params.toArray());
    }

}
