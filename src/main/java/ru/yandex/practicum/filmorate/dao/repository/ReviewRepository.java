package ru.yandex.practicum.filmorate.dao.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

@Repository
public class ReviewRepository extends BaseRepository<Review> {
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM reviews WHERE review_id = ?";
    private static final String FIND_BY_FILM_QUERY =
            "SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC LIMIT ?";
    private static final String FIND_ALL_QUERY =
            "SELECT * FROM reviews ORDER BY useful DESC LIMIT ?";
    private static final String INSERT_QUERY =
            "INSERT INTO reviews(content, is_positive, user_id, film_id, useful) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY =
            "UPDATE reviews SET content = ?, is_positive = ?, useful = ? WHERE review_id = ?";
    private static final String DELETE_QUERY = "DELETE FROM reviews WHERE review_id = ?";
    private static final String EXISTS_BY_USER_AND_FILM_QUERY =
            "SELECT COUNT(*) FROM reviews WHERE user_id = ? AND film_id = ?";
    private static final String UPDATE_USEFUL_QUERY =
            "UPDATE reviews SET useful = useful + ? WHERE review_id = ?";

    public ReviewRepository(JdbcTemplate jdbc, RowMapper<Review> mapper) {
        super(jdbc, mapper);
    }

    // Найти отзыв по его ID
    public Optional<Review> findById(long reviewId) {
        return findOne(FIND_BY_ID_QUERY, reviewId);
    }

    // Найти отзывы на фильм по его ID
    public List<Review> findByFilmId(long filmId, int count) {
        return findMany(FIND_BY_FILM_QUERY, filmId, count);
    }

    // Найти все отзывы
    public List<Review> findAll(int count) {
        return findMany(FIND_ALL_QUERY, count);
    }

    // Добавить отзыв
    public Review add(Review review) {
        long id = insert(
                INSERT_QUERY,
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId(),
                review.getUseful()
        );
        review.setReviewId(id);
        return review;
    }

    // Обновить отзыв
    public Review update(Review review) {
        update(
                UPDATE_QUERY,
                review.getContent(),
                review.getIsPositive(),
                review.getUseful(),
                review.getReviewId()
        );
        return review;
    }

    // Удалить отзыв по ID
    public boolean remove(long reviewId) {
        return delete(DELETE_QUERY, reviewId);
    }

    // Узнать, существует ли отзыв на конкретный фильм у конкретного пользователя по ID
    public boolean existsByUserAndFilm(long userId, long filmId) {
        Integer count = jdbc.queryForObject(EXISTS_BY_USER_AND_FILM_QUERY, Integer.class, userId, filmId);
        return count != null && count > 0;
    }

    // Обновить полезность при лайках
    public void updateUseful(long reviewId, int delta) {
        jdbc.update(UPDATE_USEFUL_QUERY, delta, reviewId);
    }
}
