package ru.yandex.practicum.filmorate.dao.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.film_genre.FilmGenreDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class FilmGenreRepository extends BaseRepository<FilmGenreDto> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM film_genre WHERE film_id = ? order by genre_id";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM film_genre WHERE film_id = ? AND genre_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO film_genre(film_id, genre_id) " +
            "VALUES (?, ?)";
    private static final String INSERT_GENRES_IN_FILM_QUERY = "INSERT INTO film_genre(film_id, genre_id) VALUES (?, ?)";
    private static final String REMOVE_FILM_GENRE_QUERY = "DELETE FROM film_genre WHERE film_id = ? AND genre_id = ?";
    private static final String REMOVE_FILM_GENRES_QUERY = "DELETE FROM film_genre WHERE film_id = ? AND genre_id IN (%s)";

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

    public List<FilmGenreDto> addGenresToFilm(long filmId, List<Long> genreIds) {
        if (genreIds == null || genreIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Object[]> batchArgs = genreIds.stream()
                .map(genreId -> new Object[]{filmId, genreId})
                .collect(Collectors.toList());

        jdbc.batchUpdate(INSERT_GENRES_IN_FILM_QUERY, batchArgs);

        return genreIds.stream()
                .map(genreId -> {
                    FilmGenreDto dto = new FilmGenreDto();
                    dto.setFilmId(filmId);
                    dto.setGenreId(genreId);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public boolean remove(long filmId, long genreId) {
        return jdbc.update(REMOVE_FILM_GENRE_QUERY, filmId, genreId) > 0;
    }

    public boolean removeGenres(long filmId, List<Long> genreIds) {
        if (genreIds == null || genreIds.isEmpty()) {
            return false;
        }

        String placeholders = String.join(",",
                Collections.nCopies(genreIds.size(), "?"));

        String sql = String.format(
                REMOVE_FILM_GENRES_QUERY,
                placeholders
        );

        List<Object> params = new ArrayList<>();
        params.add(filmId);
        params.addAll(genreIds);

        return jdbc.update(sql, params.toArray()) > 0;
    }

}
