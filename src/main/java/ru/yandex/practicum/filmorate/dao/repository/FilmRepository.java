package ru.yandex.practicum.filmorate.dao.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.Date;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public class FilmRepository extends BaseRepository<Film> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM films";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM films WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO films(name, description, releaseDate, duration, rating_id) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE films " +
            "SET name = ?, description = ?, releaseDate = ?, duration = ?, rating_id = ? " +
            "WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM films WHERE id = ?";
    private static final String FIND_POPULAR_FILM_QUERY =
            "SELECT f.ID, f.NAME, f.DESCRIPTION, f.RELEASEDATE, f.DURATION, f.RATING_ID " +
                    "FROM FILMS f " +
                    "LEFT JOIN LIKES l ON f.ID = l.FILM_ID " +
                    "GROUP BY f.ID, f.NAME, f.DESCRIPTION, f.RELEASEDATE, f.DURATION, f.RATING_ID " +
                    "ORDER BY COUNT(l.ID) DESC " +
                    "LIMIT ?";
    private static final String COMMON_FILMS_QUERY =
            "SELECT * FROM films f " +
                    "WHERE f.id IN (SELECT film_id FROM likes WHERE user_id = ?) " +
                    "AND f.id IN (SELECT film_id FROM likes WHERE user_id = ?) " +
                    "ORDER BY (SELECT COUNT(*) FROM likes l WHERE l.film_id = f.id) DESC";

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
        return film;
    }

    // Получение популярных фильмов, судя по лайкам
    public Collection<Film> getPopular(long count) {
        return findMany(
                FIND_POPULAR_FILM_QUERY,
                count
        );
    }

    // Удаление фильма по id
    public void remove(long filmId) {
        delete(DELETE_QUERY, filmId);
    }

    //Получение общих фильмов
    public List<Film> getCommonFilms(long userId, long friendId) {
        return findMany(COMMON_FILMS_QUERY, userId, friendId);
    }
}
