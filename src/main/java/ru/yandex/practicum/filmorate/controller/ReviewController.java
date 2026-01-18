package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.review.ReviewRequest;
import ru.yandex.practicum.filmorate.dto.review.ReviewUpdateRequest;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    // Создание отзыва
    @PostMapping
    public Review createReview(@RequestBody ReviewRequest request) {
        log.info("POST /reviews - Creating review: {}", request);
        return reviewService.createReview(request);
    }

    // Обновление отзыва
    @PutMapping
    public Review updateReview(@RequestBody ReviewUpdateRequest request) {
        log.info("PUT /reviews - Updating review: {}", request);
        return reviewService.updateReview(request);
    }

    // Получение отзыва
    @GetMapping("/{id}")
    public Review getReview(@PathVariable Long id) {
        log.info("GET /reviews/{}", id);
        return reviewService.getReview(id);
    }

    // Удаление отзыва
    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable Long id) {
        log.info("DELETE /reviews/{}", id);
        reviewService.deleteReview(id);
    }

    // Получение отзывов
    @GetMapping
    public List<Review> getReviews(
            @RequestParam(required = false) Long filmId,
            @RequestParam(defaultValue = "10") Integer count) {
        log.info("GET /reviews?filmId={}&count={}", filmId, count);
        return reviewService.getReviews(filmId, count);
    }

    // Поставить лайк отзыву
    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("PUT /reviews/{}/like/{}", id, userId);
        reviewService.addLike(id, userId);
    }

    // Убрать лайк с отзыва
    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("DELETE /reviews/{}/like/{}", id, userId);
        reviewService.removeLike(id, userId);
    }

    // Поставить дизлайк
    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("PUT /reviews/{}/dislike/{}", id, userId);
        reviewService.addDislike(id, userId);
    }

    // Убрать дизлайк
    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("DELETE /reviews/{}/dislike/{}", id, userId);
        reviewService.removeDislike(id, userId);
    }
}
