package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.repository.StatisticsRepository;
import ru.yandex.practicum.filmorate.dto.statistics.GenreYearStatistic;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Recommendation;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final StatisticsRepository statisticsRepository;
    private final UserStorage userStorage;
    private final FilmService filmService;
    private final GenreService genreService;

    // Получение рекомендаций для пользователя
    public Recommendation getRecommendations(Long userId) {
        // Проверяем существование пользователя
        userStorage.getUserById(userId);

        // Получаем рекомендации разными методами
        List<Film> collaborativeFilms = statisticsRepository.getRecommendationsForUser(userId);
        List<Film> genreBasedFilms = statisticsRepository.getGenreBasedRecommendations(userId);

        // Объединяем и убираем дубликаты
        Set<Film> allRecommendations = new LinkedHashSet<>();
        allRecommendations.addAll(collaborativeFilms);
        allRecommendations.addAll(genreBasedFilms);

        // Обогащаем фильмы полной информацией
        Set<Film> enrichedFilms = new LinkedHashSet<>();
        for (Film film : allRecommendations) {
            enrichedFilms.add(filmService.getFilmById(film.getId()));
        }

        return Recommendation.builder()
                .userId(userId)
                .recommendedFilms(enrichedFilms)
                .build();
    }

    // Получение популярных фильмов по жанру и году
    public List<Film> getPopularFilmsByGenreAndYear(Long genreId, Integer year, Long limit) {
        // Валидация параметров
        if (genreId != null && genreId <= 0) {
            throw new ObjectNotFoundException("Genre ID должен быть положительным");
        }

        if (year != null && (year < 1895 || year > Calendar.getInstance().get(Calendar.YEAR) + 1)) {
            throw new ObjectNotFoundException("Год должен быть в диапазоне 1895-" + (Calendar.getInstance().get(Calendar.YEAR) + 1));
        }

        if (limit == null || limit <= 0) {
            limit = 10L;
        }

        List<Film> films = statisticsRepository.getPopularFilmsByGenreAndYear(genreId, year, limit);

        // Обогащаем фильмы полной информацией
        List<Film> result = new ArrayList<>();
        for (Film film : films) {
            result.add(filmService.getFilmById(film.getId()));
        }
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
                GenreYearStatistic topStat = yearStats.get(0);
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
        String sql = """
            SELECT 
                g.id as genreId,
                g.name as genreName,
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
        }, limit != null ? limit : 10);
    }
}