package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.dto.review.ReviewRequest;
import ru.yandex.practicum.filmorate.dto.review.ReviewUpdateRequest;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {

    // Добавление Отзыва
    Review createReview(ReviewRequest request);

    // Обновление отзыва
    Review updateReview(ReviewUpdateRequest request);

    // Получение отзыва по id
    Review getReview(long reviewId);

    // Удаление отзыва по id
    void deleteReview(long reviewId);

    // Получение определенного количества отзывов
    List<Review> getReviews(Long filmId, int count);

    // Оставлял ли пользователь отзыв на фильм
    boolean hasUserReviewForFilm(long userId, long filmId);

    // Добавление лайка отзыву
    void addLike(long reviewId, long userId);

    // Удаление лайка у отзыва
    void removeLike(long reviewId, long userId);

    // Добавление дизлайка отзыву
    void addDislike(long reviewId, long userId);

    // Удаление дизлайка у отзыва
    void removeDislike(long reviewId, long userId);
}