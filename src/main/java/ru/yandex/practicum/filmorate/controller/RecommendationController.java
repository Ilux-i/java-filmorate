package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.RecommendationService;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    // Получение рекомендаций для пользователя
    @GetMapping("/users/{userId}")
    public ResponseEntity<List<Film>> getUserRecommendations(@PathVariable Long userId) {
        log.info("=== START Получение рекомендаций для пользователя с ID: {} ===", userId);
        try {
            List<Film> recommendations = recommendationService.getRecommendations(userId);
            log.info("=== SUCCESS Рекомендации для пользователя {} получены, количество: {} ===",
                    userId, recommendations.size());
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            log.error("=== ERROR Ошибка при получении рекомендаций для пользователя {}: {} ===",
                    userId, e.getMessage(), e);
            throw e;
        }
    }

    // Получение расширенной статистики
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        log.info("=== START Получение расширенной статистики ===");
        try {
            Map<String, Object> statistics = recommendationService.getExtendedStatistics();
            log.info("=== SUCCESS Статистика получена ===");
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            log.error("=== ERROR Ошибка при получении статистики: {} ===", e.getMessage(), e);
            throw e;
        }
    }

    // Получение топовых жанров
    @GetMapping("/genres/top")
    public ResponseEntity<List<Map<String, Object>>> getTopGenres(@RequestParam(defaultValue = "10") Long limit) {
        log.info("=== START Получение топ-{} жанров ===", limit);
        try {
            List<Map<String, Object>> topGenres = recommendationService.getTopGenres(limit);
            log.info("=== SUCCESS Топ жанров получены, количество: {} ===", topGenres.size());
            return ResponseEntity.ok(topGenres);
        } catch (Exception e) {
            log.error("=== ERROR Ошибка при получении топовых жанров: {} ===", e.getMessage(), e);
            throw e;
        }
    }
}