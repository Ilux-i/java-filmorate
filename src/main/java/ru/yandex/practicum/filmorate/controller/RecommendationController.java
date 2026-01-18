package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    // Получение рекомендаций для пользователя - ВОЗВРАЩАЕМ ЛИСТ ФИЛЬМОВ, А НЕ ОБЪЕКТ Recommendation
    @GetMapping("/users/{userId}")
    public List<Film> getUserRecommendations(@PathVariable Long userId) {
        log.info("Получение рекомендаций для пользователя с ID: {}", userId);
        return recommendationService.getRecommendations(userId);
    }

    // Получение популярных фильмов по жанру и/или году
    @GetMapping("/popular-films")
    public List<Film> getPopularFilmsByGenreAndYear(
            @RequestParam(required = false) Long genreId,
            @RequestParam(required = false) Integer year,
            @RequestParam(defaultValue = "10") Long limit) {

        log.info("Получение популярных фильмов по жанру: {}, году: {}, лимит: {}", genreId, year, limit);
        return recommendationService.getPopularFilmsByGenreAndYear(genreId, year, limit);
    }

    // Получение расширенной статистики
    @GetMapping("/statistics")
    public Map<String, Object> getStatistics() {
        log.info("Получение расширенной статистики");
        return recommendationService.getExtendedStatistics();
    }

    // Получение топовых жанров
    @GetMapping("/genres/top")
    public List<Map<String, Object>> getTopGenres(@RequestParam(defaultValue = "10") Long limit) {
        log.info("Получение топ-{} жанров", limit);
        return recommendationService.getTopGenres(limit);
    }
}