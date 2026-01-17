package ru.yandex.practicum.filmorate.dao.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.mappers.RatingRowMapper;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

@Repository
public class RatingRepository extends BaseRepository<Mpa> {
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM mpa WHERE id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM mpa";

    // Используем конкретный тип RatingRowMapper
    public RatingRepository(JdbcTemplate jdbc, RatingRowMapper mapper) {
        super(jdbc, mapper);
    }

    // Получение рейтинга по id
    public Optional<Mpa> findById(long ratingId) {
        return findOne(FIND_BY_ID_QUERY, ratingId);
    }

    // Получение списка всех рейтингов
    public List<Mpa> findAll() {
        return findMany(FIND_ALL_QUERY);
    }
}