package ru.yandex.practicum.filmorate.dao.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.Date;
import java.util.*;

@Repository
public class FilmRepository extends BaseRepository<Film> {
    private static final String FIND_ALL_QUERY = """
            SELECT *
            FROM films
            """;

    private static final String FIND_BY_ID_QUERY = """
            SELECT *
            FROM films
            WHERE id = ?
            """;

    private static final String INSERT_QUERY = """
            INSERT INTO films(name, description, releaseDate, duration, rating_id)
            VALUES (?, ?, ?, ?, ?)
            """;

    private static final String UPDATE_QUERY = """
            UPDATE films
            SET name = ?, description = ?, releaseDate = ?, duration = ?, rating_id = ?
            WHERE id = ?
            """;

    private static final String DELETE_QUERY = """
            DELETE
            FROM films
            WHERE id = ?
            """;

    private static final String FIND_POPULAR_FILM_QUERY = """
            SELECT f.ID, f.NAME, f.DESCRIPTION, f.RELEASEDATE, f.DURATION, f.RATING_ID
            FROM FILMS f
            LEFT JOIN LIKES l ON f.ID = l.FILM_ID
            GROUP BY f.ID, f.NAME, f.DESCRIPTION, f.RELEASEDATE, f.DURATION, f.RATING_ID
            ORDER BY COUNT(l.ID) DESC
            LIMIT ?
            """;

    private static final String COMMON_FILMS_QUERY = """
            SELECT *
            FROM films f
            WHERE f.id IN (SELECT film_id FROM likes WHERE user_id = ?)
            AND f.id IN (SELECT film_id FROM likes WHERE user_id = ?)
            ORDER BY (SELECT COUNT(*) FROM likes l WHERE l.film_id = f.id) DESC
            """;

    private static final String GET_FILMS_BY_DIRECTOR_BY_LIKES = """
            SELECT f.id, f.name, f.description, f.releaseDate, f.duration, f.rating_id,
            COUNT(l.user_id) AS likes_count
            FROM DIRECTORS as d
            JOIN FILM_DIRECTOR as fd on d.ID = fd.DIRECTOR_ID
            JOIN FILMS as f on fd.FILM_ID = f.ID
            LEFT JOIN LIKES as l on f.ID = l.FILM_ID
            WHERE d.id = ?
            GROUP BY f.id, f.name, f.description, f.releaseDate, f.duration, f.rating_id
            ORDER BY likes_count DESC
            """;

    private static final String GET_FILMS_BY_DIRECTOR_BY_YEAR = """
            SELECT f.id, f.name, f.description, f.releaseDate, f.duration, f.rating_id,
            COUNT(l.user_id) AS likes_count
            FROM DIRECTORS as d
            JOIN FILM_DIRECTOR as fd on d.ID = fd.DIRECTOR_ID
            JOIN FILMS as f on fd.FILM_ID = f.ID
            LEFT JOIN LIKES as l on f.ID = l.FILM_ID
            WHERE d.id = ?
            GROUP BY f.id, f.name, f.description, f.releaseDate, f.duration, f.rating_id
            ORDER BY EXTRACT(YEAR FROM f.releaseDate)
            """;

    private static final String GET_FILMS_BY_TITLE = """
            SELECT f.id, f.name as title, f.description, f.releaseDate, f.duration, f.rating_id,
            COUNT(l.id) as likes_count
            FROM films as f
            LEFT JOIN likes l ON f.id = l.film_id
            WHERE LOWER(f.name) LIKE LOWER(CONCAT('%', ?, '%'))
            GROUP BY f.id, f.name, f.description, f.releaseDate, f.duration, f.rating_id
            ORDER BY COUNT(l.id) DESC
            """;

    private static final String GET_FILMS_BY_DIRECTOR = """
            SELECT f.id, f.name as title, f.description, f.releaseDate, f.duration, f.rating_id,
            STRING_AGG(DISTINCT d.name, ', ') as directors,
            COUNT(DISTINCT l.id) as likes_count
            FROM films as f
            LEFT JOIN film_director as fd ON fd.film_id = f.id
            LEFT JOIN directors as d ON d.id = fd.director_id
            LEFT JOIN likes l ON f.id = l.film_id
            WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', ?, '%'))
            GROUP BY f.id
            ORDER BY COUNT(DISTINCT l.id) DESC
            """;

    private static final String GET_FILMS_BY_TITLE_OR_DIRECTOR = """
            SELECT f.id, f.name as title, f.description, f.releaseDate, f.duration, f.rating_id,
            (SELECT GROUP_CONCAT(d.name SEPARATOR ', ')
            FROM film_director fd2
            JOIN directors d ON d.id = fd2.director_id
            WHERE fd2.film_id = f.id) as directors,
            COUNT(DISTINCT l.id) as likes_count
            FROM films as f
            LEFT JOIN film_director as fd ON fd.film_id = f.id
            LEFT JOIN directors as d ON d.id = fd.director_id
            LEFT JOIN likes l ON f.id = l.film_id
            WHERE (LOWER(f.name) LIKE LOWER(CONCAT('%', ?, '%'))
            OR
            LOWER(d.name) LIKE LOWER(CONCAT('%', ?, '%')))
            GROUP BY f.id, f.name, f.description, f.releaseDate, f.duration, f.rating_id
            ORDER BY COUNT(DISTINCT l.id) DESC
            """;

    public FilmRepository(JdbcTemplate jdbc, FilmRowMapper mapper) {
        super(jdbc, mapper);
    }

    // Получение всех фильмов
    public List<Film> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    // Получение фильма по id
    public Optional<Film> findById(long filmId) {
        return findOne(FIND_BY_ID_QUERY, filmId);
    }

    // Добавление фильма
    public Film add(Film film) {
        long id = insert(
                INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId()
        );
        film.setId(id);
        return film;
    }

    // Обновление фильма
    public Film update(Film film) {
        update(
                UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );
        // Загружаем обновленные данные
        return findById(film.getId())
                .orElseThrow(() -> new ObjectNotFoundException("Film disappeared after update"));
    }

    // Удаление фильма по id
    public void remove(long filmId) {
        delete(DELETE_QUERY, filmId);
    }

    //Получение общих фильмов
    public List<Film> getCommonFilms(long userId, long friendId) {
        return findMany(COMMON_FILMS_QUERY, userId, friendId);
    }

    public Collection<Film> getFilmsByDirector(Long directorId, List<String> sortBy) {
        if (sortBy.getFirst().equals("likes")) {
            return findMany(GET_FILMS_BY_DIRECTOR_BY_LIKES, directorId);
        } else if (sortBy.getFirst().equals("year")) {
            return findMany(GET_FILMS_BY_DIRECTOR_BY_YEAR, directorId);
        }
        return null;
    }

    // Поиск фильмов по названию
    public Collection<Film> searchByTitle(String query) {
        return findMany(GET_FILMS_BY_TITLE, query);
    }

    // Поиск фильмов по режиссёру
    public Collection<Film> searchByDirector(String query) {
        return findMany(GET_FILMS_BY_DIRECTOR, query);
    }

    // Поиск фильмов по режиссёру
    public Collection<Film> searchByAll(String query) {
        return findMany(GET_FILMS_BY_TITLE_OR_DIRECTOR, query, query);
    }

    // Получение популярных фильмов по жанру и году
    public Collection<Film> getPopular(Long genreId, Integer year, Long limit) {
        if (genreId == null && year == null) {
            return findMany(FIND_POPULAR_FILM_QUERY, limit);
        } else {
            return getPopularWithFilters(genreId, year, limit);
        }
    }

    private Collection<Film> getPopularWithFilters(Long genreId, Integer year, Long limit) {
        List<Object> params = new ArrayList<>();

        // Базовый запрос без конечных частей
        String baseQuery = """
        SELECT f.ID, f.NAME, f.DESCRIPTION, f.RELEASEDATE, f.DURATION, f.RATING_ID
        FROM FILMS f
        LEFT JOIN LIKES l ON f.ID = l.FILM_ID
        """;

        StringBuilder sql = new StringBuilder(baseQuery);

        // Добавляем JOIN для жанра
        if (genreId != null) {
            sql.append("INNER JOIN FILM_GENRE fg ON f.ID = fg.FILM_ID ");
        }

        // Добавляем WHERE если есть фильтры
        boolean hasCondition = false;
        if (genreId != null || year != null) {
            sql.append("WHERE ");
            hasCondition = true;
        }

        if (genreId != null) {
            sql.append("fg.GENRE_ID = ? ");
            params.add(genreId);

            if (year != null) {
                sql.append("AND ");
            }
        }

        if (year != null) {
            sql.append("EXTRACT(YEAR FROM f.RELEASEDATE) = ? ");
            params.add(year);
        }

        // Всегда добавляем GROUP BY и ORDER BY
        sql.append("GROUP BY f.ID, f.NAME, f.DESCRIPTION, f.RELEASEDATE, f.DURATION, f.RATING_ID ");
        sql.append("ORDER BY COUNT(l.ID) DESC ");

        // Добавляем LIMIT
        sql.append("LIMIT ? ");
        params.add(limit);

        return jdbc.query(sql.toString(), mapper, params.toArray());
    }

}
