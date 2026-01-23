package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.repository.StatisticsRepository;
import ru.yandex.practicum.filmorate.dto.statistics.GenreYearStatistic;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final StatisticsRepository statisticsRepository;
    private final UserStorage userStorage;
    private final GenreService genreService;
    private final FilmStorage filmStorage;

    public Collection<Film> getRecommendations(Long userId) {
        log.info("====== RecommendationService.getRecommendations ВЫЗВАН ДЛЯ userId: {} ======", userId);
        // Проверяем существование пользователя
        userStorage.getUserById(userId);

        // Получаем рекомендации только на основе коллаборативной фильтрации
        List<Film> recommendations = statisticsRepository.getRecommendationsForUser(userId);

        log.debug("Коллаборативные рекомендации для пользователя {}: {} фильмов",
                userId, recommendations.size());

        // Обогащаем фильмы полной информацией
        Collection<Film> result = filmStorage.fullFilms(recommendations);

        log.info("====== RecommendationService.getRecommendations УСПЕШНО ВЕРНУЛ {} РЕКОМЕНДАЦИЙ ДЛЯ userId: {} ======",
                result.size(), userId);
        return result;
    }

    // Получение расширенной статистики
    public Map<String, Object> getExtendedStatistics() {
        List<GenreYearStatistic> statistics = statisticsRepository.getGenreYearStatistics();

        // Группируем по годам
        Map<Integer, List<GenreYearStatistic>> byYear = new TreeMap<>(Collections.reverseOrder());
        for (GenreYearStatistic stat : statistics) {
            byYear.computeIfAbsent(stat.getYear(), k -> new ArrayList<>()).add(stat);
        }

        // Группируем по жанрам
        Map<Long, List<GenreYearStatistic>> byGenre = new HashMap<>();
        for (GenreYearStatistic stat : statistics) {
            byGenre.computeIfAbsent(stat.getGenreId(), k -> new ArrayList<>()).add(stat);
        }

        // Находим топовые жанры по годам
        Map<Integer, Map<String, Object>> topGenresByYear = new HashMap<>();
        for (Map.Entry<Integer, List<GenreYearStatistic>> entry : byYear.entrySet()) {
            Integer year = entry.getKey();
            List<GenreYearStatistic> yearStats = entry.getValue();

            if (!yearStats.isEmpty()) {
                GenreYearStatistic topStat = yearStats.getFirst();
                Map<String, Object> topGenreInfo = new HashMap<>();
                topGenreInfo.put("genreId", topStat.getGenreId());
                topGenreInfo.put("genreName", genreService.getGenre(topStat.getGenreId()).getName());
                topGenreInfo.put("likeCount", topStat.getLikeCount());
                topGenreInfo.put("filmCount", topStat.getFilmCount());
                topGenresByYear.put(year, topGenreInfo);
            }
        }

        // Формируем результат
        Map<String, Object> result = new HashMap<>();
        result.put("totalStatisticRecords", statistics.size());
        result.put("statisticsByYear", byYear);
        result.put("topGenresByYear", topGenresByYear);
        result.put("yearsCovered", byYear.keySet().size());

        return result;
    }

    // Получение топовых жанров
    public List<Map<String, Object>> getTopGenres(Long limit) {
        if (limit == null || limit <= 0) {
            limit = 10L;
        }

        String sql = """
                    SELECT g.id as genreId, g.name as genreName,
                    COUNT(DISTINCT l.id) as totalLikes,
                    COUNT(DISTINCT f.id) as totalFilms
                    FROM genres g
                    LEFT JOIN film_genre fg ON g.id = fg.genre_id
                    LEFT JOIN films f ON fg.film_id = f.id
                    LEFT JOIN likes l ON f.id = l.film_id
                    GROUP BY g.id, g.name
                    ORDER BY totalLikes DESC, totalFilms DESC
                    LIMIT ?
                    """;

        return statisticsRepository.getJdbcTemplate().query(sql, (rs, rowNum) -> {
                Map<String, Object> genreStat = new HashMap<>();
                genreStat.put("genreId", rs.getLong("genreId"));
                genreStat.put("genreName", rs.getString("genreName"));
                genreStat.put("totalLikes", rs.getLong("totalLikes"));
                genreStat.put("totalFilms", rs.getLong("totalFilms"));
                return genreStat;
            }, limit);
    }
}