package ru.yandex.practicum.filmorate.dao.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;

import java.util.Optional;

@Repository
public class GenreRepository extends BaseRepository<GenreDto> {
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genre WHERE id = ?";

    public GenreRepository(JdbcTemplate jdbc, RowMapper<GenreDto> mapper) {
        super(jdbc, mapper);
    }

    public Optional<GenreDto> findById(long genreId) {
        return findOne(FIND_BY_ID_QUERY, genreId);
    }
}
