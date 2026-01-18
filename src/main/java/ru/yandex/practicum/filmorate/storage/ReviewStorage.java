package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.dto.review.ReviewRequest;
import ru.yandex.practicum.filmorate.dto.review.ReviewUpdateRequest;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {
    Review createReview(ReviewRequest request);
    Review updateReview(ReviewUpdateRequest request);
    Review getReview(long reviewId);
    void deleteReview(long reviewId);
    List<Review> getReviews(Long filmId, int count);
    boolean hasUserReviewForFilm(long userId, long filmId);

    void addLike(long reviewId, long userId);
    void removeLike(long reviewId, long userId);
    void addDislike(long reviewId, long userId);
    void removeDislike(long reviewId, long userId);
}
