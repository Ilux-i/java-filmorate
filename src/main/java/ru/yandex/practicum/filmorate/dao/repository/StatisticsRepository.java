package ru.yandex.practicum.filmorate.dao.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.statistics.GenreYearStatistic;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.dao.mappers.FilmRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class StatisticsRepository {

    private final JdbcTemplate jdbc;
    private final FilmRowMapper filmRowMapper;

    // Получение самых популярных фильмов по жанру и году
    public List<Film> getPopularFilmsByGenreAndYear(Long genreId, Integer year, Long limit) {
        String sql = """
            SELECT f.* 
            FROM films f
            LEFT JOIN film_genre fg ON f.id = fg.film_id
            LEFT JOIN likes l ON f.id = l.film_id
            WHERE (? IS NULL OR fg.genre_id = ?)
              AND (? IS NULL OR YEAR(f.releaseDate) = ?)
            GROUP BY f.id
            ORDER BY COUNT(l.id) DESC
            LIMIT ?
            """;

        return jdbc.query(sql, filmRowMapper, genreId, genreId, year, year, limit);
    }

    // Получение статистики по жанрам и годам
    public List<GenreYearStatistic> getGenreYearStatistics() {
        String sql = """
            SELECT 
                YEAR(f.releaseDate) as year,
                fg.genre_id as genreId,
                COUNT(DISTINCT l.id) as likeCount,
                COUNT(DISTINCT f.id) as filmCount
            FROM films f
            LEFT JOIN film_genre fg ON f.id = fg.film_id
            LEFT JOIN likes l ON f.id = l.film_id
            WHERE fg.genre_id IS NOT NULL
            GROUP BY YEAR(f.releaseDate), fg.genre_id
            ORDER BY year DESC, likeCount DESC
            """;

        return jdbc.query(sql, this::mapToGenreYearStatistic);
    }

    // Получение рекомендаций для пользователя на основе коллаборативной фильтрации
    public List<Film> getRecommendationsForUser(Long userId) {
        // Находим пользователей с похожими вкусами
        String similarUsersSql = """
            SELECT l2.user_id
            FROM likes l1
            JOIN likes l2 ON l1.film_id = l2.film_id
            WHERE l1.user_id = ?
              AND l2.user_id != ?
            GROUP BY l2.user_id
            ORDER BY COUNT(DISTINCT l1.film_id) DESC
            LIMIT 5
            """;

        List<Long> similarUserIds = jdbc.queryForList(
                similarUsersSql, Long.class, userId, userId);

        if (similarUserIds.isEmpty()) {
            return getPopularFilms(10L);
        }

        // Находим фильмы, которые понравились похожим пользователям, но не текущему
        String placeholders = String.join(",",
                Collections.nCopies(similarUserIds.size(), "?"));

        String recommendationsSql = String.format("""
            SELECT f.*
            FROM films f
            JOIN likes l ON f.id = l.film_id
            WHERE l.user_id IN (%s)
              AND f.id NOT IN (
                  SELECT film_id 
                  FROM likes 
                  WHERE user_id = ?
              )
            GROUP BY f.id
            ORDER BY COUNT(l.id) DESC
            LIMIT 10
            """, placeholders);

        List<Object> params = new ArrayList<>(similarUserIds);
        params.add(userId);

        return jdbc.query(recommendationsSql, filmRowMapper, params.toArray());
    }

    // Получение рекомендаций на основе жанров пользователя
    public List<Film> getGenreBasedRecommendations(Long userId) {
        String sql = """
            SELECT f.*
            FROM films f
            JOIN film_genre fg ON f.id = fg.film_id
            WHERE fg.genre_id IN (
                SELECT DISTINCT fg2.genre_id
                FROM likes l
                JOIN film_genre fg2 ON l.film_id = fg2.film_id
                WHERE l.user_id = ?
            )
            AND f.id NOT IN (
                SELECT film_id 
                FROM likes 
                WHERE user_id = ?
            )
            GROUP BY f.id
            ORDER BY (
                SELECT COUNT(*)
                FROM likes l2
                WHERE l2.film_id = f.id
            ) DESC
            LIMIT 10
            """;

        return jdbc.query(sql, filmRowMapper, userId, userId);
    }

    // Получение популярных фильмов
    private List<Film> getPopularFilms(Long limit) {
        String sql = """
            SELECT f.* 
            FROM films f
            LEFT JOIN likes l ON f.id = l.film_id
            GROUP BY f.id
            ORDER BY COUNT(l.id) DESC
            LIMIT ?
            """;

        return jdbc.query(sql, filmRowMapper, limit);
    }

    // Маппер для статистики
    private GenreYearStatistic mapToGenreYearStatistic(ResultSet rs, int rowNum) throws SQLException {
        GenreYearStatistic stat = new GenreYearStatistic();
        stat.setYear(rs.getInt("year"));
        stat.setGenreId(rs.getLong("genreId"));
        stat.setLikeCount(rs.getLong("likeCount"));
        stat.setFilmCount(rs.getLong("filmCount"));
        return stat;
    }

    // Метод для доступа к JdbcTemplate из других сервисов
    public JdbcTemplate getJdbcTemplate() {
        return jdbc;
    }
}