package ru.yandex.practicum.filmorate.dao.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Feed;

import java.util.List;

@Repository
public class FeedRepository extends BaseRepository<Feed> {

    private static final String FIND_BY_USER_ID_QUERY = """
            SELECT *
            FROM feeds
            WHERE user_id = ?
            ORDER BY event_id
            """;

    private static final String INSERT_QUERY = """
            INSERT INTO feeds (timestamp, user_id, event_type, operation, entity_id)
            VALUES (?, ?, ?, ?, ?)
            """;

    public FeedRepository(JdbcTemplate jdbc, RowMapper<Feed> mapper) {
        super(jdbc, mapper);
    }

    // поиск события по id пользователя
    public List<Feed> findByUserId(long userId) {
        return findMany(FIND_BY_USER_ID_QUERY, userId);
    }

    // Добавление события
    public Feed add(Feed feed) {
        long id = insert(INSERT_QUERY,
                feed.getTimestamp(),
                feed.getUserId(),
                feed.getEventType().name(),
                feed.getOperation().name(),
                feed.getEntityId()
        );
        feed.setEventId(id);
        return feed;
    }
}
