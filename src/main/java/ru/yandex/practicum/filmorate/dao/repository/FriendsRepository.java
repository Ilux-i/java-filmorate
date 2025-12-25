package ru.yandex.practicum.filmorate.dao.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.friend.PairFriendDto;
import ru.yandex.practicum.filmorate.dto.friend.FriendDto;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;

import java.util.List;

@Repository
public class FriendsRepository extends BaseRepository<FriendDto> {

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
    private static final String INSERT_QUERY = "INSERT INTO friends(user_id, friend_id, status) " +
            "VALUES (?, ?, ?)";
    private static final String CONFIRM_FRIEND_QUERY = "UPDATE friends SET status = " + FriendshipStatus.CONFIRMED +
            " WHERE user_id = ? AND friend_id = ?";
    private static final String REMOVE_FRIEND_QUERY = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";

    public FriendsRepository(JdbcTemplate jdbc, RowMapper<FriendDto> mapper) {
        super(jdbc, mapper);
    }

    public List<FriendDto> findFriendsByUserId(long userId) {
        return findMany(FIND_FRIENDS_BY_ID_QUERY, userId);
    }

    public List<FriendDto> findFriendRequestsByUserId(long userId) {
        return findMany(
                FIND_FRIEND_REQUESTS_BY_ID_QUERY,
                userId,
                FriendshipStatus.CONFIRMED.toString()
        );
    }

    public long addFriend(PairFriendDto dto) {
        return insert(
                INSERT_QUERY,
                dto.getUserId(),
                dto.getFriendId(),
                FriendshipStatus.CONFIRMED.toString()
        );
    }

    public long confirmFriend(PairFriendDto dto) {
        return insert(
                CONFIRM_FRIEND_QUERY,
                dto.getUserId(),
                dto.getFriendId()
        );
    }

    public boolean remove(PairFriendDto dto) {
        return jdbc.update(REMOVE_FRIEND_QUERY, dto.getUserId(), dto.getFriendId()) > 0;
    }

}
