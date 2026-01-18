package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.repository.StatisticsRepository;
import ru.yandex.practicum.filmorate.dto.statistics.GenreYearStatistic;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.UserStorage; // Добавьте эту строку

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final StatisticsRepository statisticsRepository;
    private final UserStorage userStorage; // Этот импорт теперь будет работать
    private final FilmService filmService;
    private final GenreService genreService;

    public List<Film> getRecommendations(Long userId) {
        log.info("Начало получения рекомендаций для пользователя с ID: {}", userId);
        try {
            // Проверяем существование пользователя
            log.debug("Проверка пользователя с ID: {}", userId);
            userStorage.getUserById(userId);
            log.debug("Пользователь с ID: {} найден", userId);

            // Получаем рекомендации разными методами
            log.debug("Получение коллаборативных рекомендаций для пользователя с ID: {}", userId);
            List<Film> collaborativeFilms;
            try {
                collaborativeFilms = statisticsRepository.getRecommendationsForUser(userId);
                log.debug("Коллаборативные рекомендации для пользователя с ID: {} получены, количество: {}",
                        userId, collaborativeFilms.size());
            } catch (Exception e) {
                log.error("Ошибка при получении коллаборативных рекомендаций для пользователя {}: {}",
                        userId, e.getMessage(), e);
                collaborativeFilms = new ArrayList<>();
            }

            log.debug("Получение рекомендаций по жанрам для пользователя с ID: {}", userId);
            List<Film> genreBasedFilms;
            try {
                genreBasedFilms = statisticsRepository.getGenreBasedRecommendations(userId);
                log.debug("Рекомендации по жанрам для пользователя с ID: {} получены, количество: {}",
                        userId, genreBasedFilms.size());
            } catch (Exception e) {
                log.error("Ошибка при получении рекомендаций по жанрам для пользователя {}: {}",
                        userId, e.getMessage(), e);
                genreBasedFilms = new ArrayList<>();
            }

            // Объединяем и убираем дубликаты
            Set<Film> allRecommendations = new LinkedHashSet<>();
            allRecommendations.addAll(collaborativeFilms);
            allRecommendations.addAll(genreBasedFilms);
            log.debug("Всего уникальных рекомендаций для пользователя с ID: {}: {}",
                    userId, allRecommendations.size());

            // Обогащаем фильмы полной информацией и преобразуем в List
            List<Film> result = new ArrayList<>();
            for (Film film : allRecommendations) {
                try {
                    Film fullFilm = filmService.getFilmById(film.getId());
                    result.add(fullFilm);
                } catch (Exception e) {
                    log.warn("Фильм с id {} не найден, пропускаем. Ошибка: {}", film.getId(), e.getMessage());
                }
            }

            log.info("Рекомендации для пользователя с ID: {} успешно получены, количество: {}",
                    userId, result.size());
            return result;
        } catch (ObjectNotFoundException e) {
            log.error("Пользователь с ID: {} не найден", userId, e);
            throw e;
        } catch (Exception e) {
            log.error("Ошибка при получении рекомендаций для пользователя {}: {}", userId, e.getMessage(), e);
            throw new InternalServerException("Ошибка при получении рекомендаций: " + e.getMessage());
        }
    }

    public List<Film> getPopularFilmsByGenreAndYear(Long genreId, Integer year, Long limit) {
        log.info("Начало получения популярных фильмов: genreId={}, year={}, limit={}", genreId, year, limit);
        try {
            // Валидация параметров
            if (genreId != null && genreId <= 0) {
                throw new ObjectNotFoundException("Genre ID должен быть положительным");
            }

            if (year != null && (year < 1895 || year > Calendar.getInstance().get(Calendar.YEAR) + 1)) {
                throw new ObjectNotFoundException("Год должен быть в диапазоне 1895-" +
                        (Calendar.getInstance().get(Calendar.YEAR) + 1));
            }

            if (limit == null || limit <= 0) {
                limit = 10L;
                log.debug("Лимит установлен по умолчанию: {}", limit);
            }

            log.debug("Вызов statisticsRepository.getPopularFilmsByGenreAndYear");
            List<Film> films = statisticsRepository.getPopularFilmsByGenreAndYear(genreId, year, limit);
            log.debug("Получено {} фильмов из репозитория", films.size());

            // Обогащаем фильмы полной информацией
            List<Film> result = new ArrayList<>();
            for (Film film : films) {
                try {
                    Film fullFilm = filmService.getFilmById(film.getId());
                    result.add(fullFilm);
                } catch (Exception e) {
                    log.warn("Фильм с id {} не найден, пропускаем. Ошибка: {}", film.getId(), e.getMessage());
                }
            }
            log.info("Успешно возвращено {} популярных фильмов", result.size());
            return result;
        } catch (ObjectNotFoundException e) {
            log.error("Ошибка валидации параметров: ", e);
            throw e;
        } catch (Exception e) {
            log.error("Ошибка при получении популярных фильмов: жанр={}, год={}, лимит={}: {}",
                    genreId, year, limit, e.getMessage(), e);
            throw new InternalServerException("Ошибка при получении популярных фильмов: " + e.getMessage());
        }
    }

    // Получение расширенной статистики
    public Map<String, Object> getExtendedStatistics() {
        try {
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
                    try {
                        topGenreInfo.put("genreName", genreService.getGenre(topStat.getGenreId()).getName());
                    } catch (Exception e) {
                        topGenreInfo.put("genreName", "Неизвестный жанр");
                    }
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
        } catch (Exception e) {
            log.error("Ошибка при получении статистики: {}", e.getMessage(), e);
            throw new InternalServerException("Ошибка при получении статистики: " + e.getMessage());
        }
    }

    // Получение топовых жанров
    public List<Map<String, Object>> getTopGenres(Long limit) {
        try {
            if (limit == null || limit <= 0) {
                limit = 10L;
            }

            String sql = "SELECT " +
                    "    g.id as genreId, " +
                    "    g.name as genreName, " +
                    "    COUNT(DISTINCT l.id) as totalLikes, " +
                    "    COUNT(DISTINCT f.id) as totalFilms " +
                    "FROM genres g " +
                    "LEFT JOIN film_genre fg ON g.id = fg.genre_id " +
                    "LEFT JOIN films f ON fg.film_id = f.id " +
                    "LEFT JOIN likes l ON f.id = l.film_id " +
                    "GROUP BY g.id, g.name " +
                    "ORDER BY totalLikes DESC, totalFilms DESC " +
                    "LIMIT ?";

            return statisticsRepository.getJdbcTemplate().query(sql, (rs, rowNum) -> {
                Map<String, Object> genreStat = new HashMap<>();
                genreStat.put("genreId", rs.getLong("genreId"));
                genreStat.put("genreName", rs.getString("genreName"));
                genreStat.put("totalLikes", rs.getLong("totalLikes"));
                genreStat.put("totalFilms", rs.getLong("totalFilms"));
                return genreStat;
            }, limit);
        } catch (Exception e) {
            log.error("Ошибка при получении топовых жанров: {}", e.getMessage(), e);
            throw new InternalServerException("Ошибка при получении топовых жанров: " + e.getMessage());
        }
    }
}