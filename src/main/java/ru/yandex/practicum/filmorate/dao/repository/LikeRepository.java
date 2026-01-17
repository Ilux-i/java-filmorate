package ru.yandex.practicum.filmorate.dao.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.mappers.LikeRowMapper;
import ru.yandex.practicum.filmorate.dto.like.LikeDto;

import java.util.List;
import java.util.Optional;

@Repository
public class LikeRepository extends BaseRepository<LikeDto> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM likes WHERE film_id = ?";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM likes WHERE film_id = ? AND user_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO likes(film_id, user_id) " +
            "VALUES (?, ?)";
    private static final String COUNT_LIKES_FILM_QUERY = "SELECT COUNT(*) FROM likes GROUP BY film_id";
    private static final String REMOVE_LIKE_QUERY = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";

    public LikeRepository(JdbcTemplate jdbc, LikeRowMapper mapper) {
        super(jdbc, mapper);
    }

    // Получение всех лайков по фильму
    public List<LikeDto> findAllByFilm(long filmId) {
        return findMany(FIND_ALL_QUERY, filmId);
    }

    // Получение одного лайка по пользователю и фильму
    public Optional<LikeDto> findById(long filmId, long userId) {
        return findOne(FIND_BY_ID_QUERY, filmId, userId);
    }

    // Добавление лайка к фильму
    public LikeDto add(LikeDto dto) {
        long id = insert(
                INSERT_QUERY,
                dto.getFilmId(),
                dto.getUserId()
        );
        dto.setId(id);
        return dto;
    }

    // Получение количества лайков по id фильма
    public long countLikesByFilmId(long filmId) {
        return jdbc.queryForObject(COUNT_LIKES_FILM_QUERY, Long.class, filmId);
    }

    // Удаление лайка по id фильма и пользователя
    public boolean remove(long filmId, long userId) {
        return jdbc.update(REMOVE_LIKE_QUERY, filmId, userId) > 0;
    }

}