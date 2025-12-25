package ru.yandex.practicum.filmorate.dao.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.film_genre.FilmGenreDto;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

@Repository
public class FilmGenreRepository extends BaseRepository<FilmGenreDto> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM film_genre WHERE film_id = ?";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM film_genre WHERE film_id = ? AND genre = ?";
    private static final String INSERT_QUERY = "INSERT INTO film_genre(film_id, genre)" +
            "VALUES (?, ?) returning id";
    private static final String REMOVE_FILM_GENRE_QUERY = "DELETE FROM film_genre WHERE film_id = ? AND genre = ?";


    public FilmGenreRepository(JdbcTemplate jdbc, RowMapper<FilmGenreDto> mapper) {
        super(jdbc, mapper);
    }

    public List<FilmGenreDto> findAllByFilm(long filmId) {
        return findMany(FIND_ALL_QUERY, filmId);
    }

    public Optional<FilmGenreDto> findById(long filmId, Genre genre) {
        return findOne(FIND_BY_ID_QUERY, filmId, genre.toString());
    }

    public FilmGenreDto add(FilmGenreDto dto) {
        long id = insert(
                INSERT_QUERY,
                dto.getFilmId(),
                dto.getGenre().toString()
        );
        dto.setId(id);
        return dto;
    }

    public boolean remove(long filmId, Genre genre) {
        return jdbc.update(REMOVE_FILM_GENRE_QUERY, filmId, genre) > 0;
    }

}
