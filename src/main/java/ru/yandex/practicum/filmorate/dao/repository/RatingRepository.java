package ru.yandex.practicum.filmorate.dao.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.raing.RatingDto;

import java.util.Optional;

@Repository
public class RatingRepository extends BaseRepository<RatingDto> {
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM rating WHERE id = ?";

    public RatingRepository(JdbcTemplate jdbc, RowMapper<RatingDto> mapper) {
        super(jdbc, mapper);
    }

    public Optional<RatingDto> findById(long ratingId) {
        return findOne(FIND_BY_ID_QUERY, ratingId);
    }
}