package ru.yandex.practicum.filmorate.dao.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.film_genre.FilmGenreDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

@Repository
public class FilmGenreRepository extends BaseRepository<FilmGenreDto> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM film_genre WHERE film_id = ?";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM film_genre WHERE film_id = ? AND genre_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO film_genre(film_id, genre_id)" +
            "VALUES (?, ?) returning id";
    private static final String REMOVE_FILM_GENRE_QUERY = "DELETE FROM film_genre WHERE film_id = ? AND genre_id = ?";


    public FilmGenreRepository(JdbcTemplate jdbc, RowMapper<FilmGenreDto> mapper) {
        super(jdbc, mapper);
    }

    public List<FilmGenreDto> findAllByFilm(long filmId) {
        return findMany(FIND_ALL_QUERY, filmId);
    }

    public Optional<FilmGenreDto> findById(long filmId, long genreId) {
        return findOne(FIND_BY_ID_QUERY, filmId, genreId);
    }

    public FilmGenreDto add(FilmGenreDto dto) {
        long id = insert(
                INSERT_QUERY,
                dto.getFilmId(),
                dto.getGenreId()
        );
        dto.setId(id);
        return dto;
    }

    public boolean remove(long filmId, long genreId) {
        return jdbc.update(REMOVE_FILM_GENRE_QUERY, filmId, genreId) > 0;
    }

}
