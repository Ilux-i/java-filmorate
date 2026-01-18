package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.review.ReviewRequest;
import ru.yandex.practicum.filmorate.dto.review.ReviewUpdateRequest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    @Qualifier("ReviewDbStorage")
    private final ReviewStorage reviewStorage;

    @Qualifier("UserDbStorage")
    private final UserStorage userStorage;

    @Qualifier("FilmDbStorage")
    private final FilmStorage filmStorage;

    // Создание отзыва
    public Review createReview(ReviewRequest request) {
        // Валидация обязательных полей
        validateReviewRequest(request);

        // Проверка существования пользователя и фильма
        userStorage.getUserById(request.getUserId());
        filmStorage.getFilmById(request.getFilmId());

        // Проверка уникальности отзыва
        if (reviewStorage.hasUserReviewForFilm(request.getUserId(), request.getFilmId())) {
            throw new ValidationException("Пользователь уже написал отзыв к этому фильму");
        }

        Review review = reviewStorage.createReview(request);
        log.info("Отзыв создан: {}", review);
        return review;
    }

    // Обновление отзыва
    public Review updateReview(ReviewUpdateRequest request) {
        // Валидация id пользователя и отзыва
        if (request.getReviewId() == null) {
            throw new ValidationException("Требуется reviewId");
        }

        Review existing = reviewStorage.getReview(request.getReviewId());

        // Проверка авторства
        if (!existing.getUserId().equals(request.getUserId())) {
            throw new ValidationException("Только автор может изменять отзыв");
        }

        // Валидация полей
        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new ValidationException("Требуется содержимое отзыва");
        }
        if (request.getIsPositive() == null) {
            throw new ValidationException("Требуется isPositive");
        }

        Review updated = reviewStorage.updateReview(request);
        log.info("Отзыв обновлен: {}", updated);
        return updated;
    }

    // Получение отзыва
    public Review getReview(long reviewId) {
        Review review = reviewStorage.getReview(reviewId);
        log.info("Отзыв получен: {}", review);
        return review;
    }

    // Удаление отзыва
    public void deleteReview(long reviewId) {
        reviewStorage.deleteReview(reviewId);
        log.info("Отзыв удален: {}", reviewId);
    }

    // Получение отзывов
    public List<Review> getReviews(Long filmId, int count) {
        // Валидация параметра количества отзывов
        if (count <= 0) {
            throw new ValidationException("Количество должно быть положительным числом");
        }

        List<Review> reviews = reviewStorage.getReviews(filmId, count);
        log.info("Получены {} отзывы от filmId: {}", reviews.size(), filmId);
        return reviews;
    }

    // Добавление лайка
    public void addLike(long reviewId, long userId) {
        userStorage.getUserById(userId);
        reviewStorage.getReview(reviewId);

        reviewStorage.addLike(reviewId, userId);
        log.info("Пользователь {} поставил лайк на отзыв {}", userId, reviewId);
    }

    // Удаление лайка
    public void removeLike(long reviewId, long userId) {
        userStorage.getUserById(userId);
        reviewStorage.getReview(reviewId);

        reviewStorage.removeLike(reviewId, userId);
        log.info("Пользователь {} убрал лайк с отзыва {}", userId, reviewId);
    }

    // Добавление дизлайка
    public void addDislike(long reviewId, long userId) {
        userStorage.getUserById(userId);
        reviewStorage.getReview(reviewId);

        reviewStorage.addDislike(reviewId, userId);
        log.info("Пользователь {} поставил дизлайк на отзыв {}", userId, reviewId);
    }

    // Удаление дизлайка
    public void removeDislike(long reviewId, long userId) {
        userStorage.getUserById(userId);
        reviewStorage.getReview(reviewId);

        reviewStorage.removeDislike(reviewId, userId);
        log.info("Пользователь {} убрал дизлайк с отзыва {}", userId, reviewId);
    }

    // Валидация всех полей при создании
    private void validateReviewRequest(ReviewRequest request) {
        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new ValidationException("Требуется содержимое отзыва");
        }
        if (request.getIsPositive() == null) {
            throw new ValidationException("Требуется isPositive");
        }
        if (request.getUserId() == null) {
            throw new ValidationException("Требуется userId");
        }
        if (request.getFilmId() == null) {
            throw new ValidationException("Требуется filmId");
        }
    }
}
