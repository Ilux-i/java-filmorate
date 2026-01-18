package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.repository.ReviewLikeRepository;
import ru.yandex.practicum.filmorate.dao.repository.ReviewRepository;
import ru.yandex.practicum.filmorate.dto.review.ReviewRequest;
import ru.yandex.practicum.filmorate.dto.review.ReviewUpdateRequest;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

@Component("ReviewDbStorage")
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {
    private final ReviewRepository reviewRepository;
    private final ReviewLikeRepository reviewLikeRepository;

    // Создание отзыва
    @Override
    public Review createReview(ReviewRequest request) {
        Review review = Review.builder()
                .content(request.getContent())
                .isPositive(request.getIsPositive())
                .userId(request.getUserId())
                .filmId(request.getFilmId())
                .useful(0)
                .build();
        return reviewRepository.add(review);
    }

    // Обновление отзыва
    @Override
    public Review updateReview(ReviewUpdateRequest request) {
        Review review = getReview(request.getReviewId());
        review.setContent(request.getContent());
        review.setIsPositive(request.getIsPositive());
        return reviewRepository.update(review);
    }

    // Получение отзыва по ID
    @Override
    public Review getReview(long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ObjectNotFoundException("Отзыв с id " + reviewId + " не найден"));
    }

    // Удаление отзыва
    @Override
    public void deleteReview(long reviewId) {
        if (!reviewRepository.remove(reviewId)) {
            throw new ObjectNotFoundException("Отзыв с id " + reviewId + " не найден");
        }
    }

    // Получение отзывов
    @Override
    public List<Review> getReviews(Long filmId, int count) {
        if (filmId != null) {
            return reviewRepository.findByFilmId(filmId, count);
        } else {
            return reviewRepository.findAll(count);
        }
    }

    // Проверка наличия отзыва
    @Override
    public boolean hasUserReviewForFilm(long userId, long filmId) {
        return reviewRepository.existsByUserAndFilm(userId, filmId);
    }

    // Добавление лайка
    @Override
    public void addLike(long reviewId, long userId) {
        Optional<Boolean> currentStatus = reviewLikeRepository.getLikeStatus(reviewId, userId);

        if (currentStatus.isEmpty()) {
            reviewLikeRepository.addLike(reviewId, userId, true);
            reviewRepository.updateUseful(reviewId, 1);
        } else if (!currentStatus.get()) {
            reviewLikeRepository.addLike(reviewId, userId, true);
            reviewRepository.updateUseful(reviewId, 2);
        }
    }

    // Удаление лайка
    @Override
    public void removeLike(long reviewId, long userId) {
        Optional<Boolean> currentStatus = reviewLikeRepository.getLikeStatus(reviewId, userId);

        if (currentStatus.isPresent() && currentStatus.get()) {
            reviewLikeRepository.removeLike(reviewId, userId);
            reviewRepository.updateUseful(reviewId, -1);
        }
    }

    // Добавление дизлайка
    @Override
    public void addDislike(long reviewId, long userId) {
        Optional<Boolean> currentStatus = reviewLikeRepository.getLikeStatus(reviewId, userId);

        if (currentStatus.isEmpty()) {
            reviewLikeRepository.addLike(reviewId, userId, false);
            reviewRepository.updateUseful(reviewId, -1);
        } else if (currentStatus.get()) {
            reviewLikeRepository.addLike(reviewId, userId, false);
            reviewRepository.updateUseful(reviewId, -2);
        }
    }

    // Удаление дизлайка
    @Override
    public void removeDislike(long reviewId, long userId) {
        Optional<Boolean> currentStatus = reviewLikeRepository.getLikeStatus(reviewId, userId);

        if (currentStatus.isPresent() && !currentStatus.get()) {
            reviewLikeRepository.removeLike(reviewId, userId);
            reviewRepository.updateUseful(reviewId, 1);
        }
    }
}
