package ru.yandex.practicum.filmorate.dao.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReviewLikeRepository {
    private final JdbcTemplate jdbc;

    private static final String INSERT_LIKE_QUERY =
            "INSERT INTO review_likes(review_id, user_id, is_like) VALUES (?, ?, ?)";
    private static final String UPDATE_LIKE_QUERY =
            "UPDATE review_likes SET is_like = ? WHERE review_id = ? AND user_id = ?";
    private static final String DELETE_LIKE_QUERY =
            "DELETE FROM review_likes WHERE review_id = ? AND user_id = ?";
    private static final String GET_LIKE_STATUS_QUERY =
            "SELECT is_like FROM review_likes WHERE review_id = ? AND user_id = ?";
    private static final String EXISTS_LIKE_QUERY =
            "SELECT COUNT(*) FROM review_likes WHERE review_id = ? AND user_id = ?";


    // Добавить лайк на конкретный отзыв
    public void addLike(long reviewId, long userId, boolean isLike) {
        // Обновление голоса, если есть
        if (exists(reviewId, userId)) {
            jdbc.update(UPDATE_LIKE_QUERY, isLike, reviewId, userId);
        // Добавление, если нет
        } else {
            jdbc.update(INSERT_LIKE_QUERY, reviewId, userId, isLike);
        }
    }

    // Убрать лайк с конкретного отзыва
    public void removeLike(long reviewId, long userId) {
        jdbc.update(DELETE_LIKE_QUERY, reviewId, userId);
    }

    // Получение статуса голоса у конкретного пользователя на конкретный фильм
    public Optional<Boolean> getLikeStatus(long reviewId, long userId) {
        try {
            Boolean result = jdbc.queryForObject(GET_LIKE_STATUS_QUERY, Boolean.class, reviewId, userId);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // Валидация на наличие отзыва у пользователя
    public boolean exists(long reviewId, long userId) {
        Integer count = jdbc.queryForObject(EXISTS_LIKE_QUERY, Integer.class, reviewId, userId);
        return count != null && count > 0;
    }
}
