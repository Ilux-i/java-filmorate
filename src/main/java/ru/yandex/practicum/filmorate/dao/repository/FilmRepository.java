package ru.yandex.practicum.filmorate.dao.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
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
    private static final String FIND_POPULAR_FILM_QUERY = "select f.ID, f.NAME, f.DESCRIPTION, f.RELEASEDATE, f.DURATION, f.RATING_ID " +
            "from FILMS as f " +
            "right join LIKES as l on f.ID = l.FILM_ID " +
            "group by f.ID " +
            "order by count(l.ID) desc " +
            "LIMIT ?";
    private static final String GET_FILMS_BY_DIRECTOR_BY_LIKES = "SELECT f.id, f.name, f.description, f.releaseDate, f.duration, f.rating_id, " +
            "COUNT(l.user_id) AS likes_count " +
            "FROM DIRECTORS as d " +
            "join FILM_DIRECTOR as fd on d.ID = fd.DIRECTOR_ID " +
            "join FILMS as f on fd.FILM_ID = f.ID " +
            "left join LIKES as l on f.ID = l.FILM_ID " +
            "WHERE d.id = ? " +
            "GROUP BY f.id, f.name, f.description, f.releaseDate, f.duration, f.rating_id " +
            "ORDER BY likes_count DESC";
    private static final String GET_FILMS_BY_DIRECTOR_BY_YEAR = "SELECT f.id, f.name, f.description, f.releaseDate, f.duration, f.rating_id, " +
            "COUNT(l.user_id) AS likes_count " +
            "FROM DIRECTORS as d " +
            "join FILM_DIRECTOR as fd on d.ID = fd.DIRECTOR_ID " +
            "join FILMS as f on fd.FILM_ID = f.ID " +
            "left join LIKES as l on f.ID = l.FILM_ID " +
            "WHERE d.id = ? " +
            "GROUP BY f.id, f.name, f.description, f.releaseDate, f.duration, f.rating_id " +
            "ORDER BY EXTRACT(YEAR FROM f.releaseDate); ";

    public FilmRepository(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    // Получение  всех фильмов
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
    public boolean remove(long filmId) {
        return delete(DELETE_QUERY, filmId);
    }

    public Collection<Film> getFilmsByDirector(Long directorId, List<String> sortBy) {
        if (sortBy.getFirst().equals("likes")) {
            return findMany(GET_FILMS_BY_DIRECTOR_BY_LIKES, directorId);
        } else if (sortBy.getFirst().equals("year")) {
            return findMany(GET_FILMS_BY_DIRECTOR_BY_YEAR, directorId);
        }
        return null;
    }
}
