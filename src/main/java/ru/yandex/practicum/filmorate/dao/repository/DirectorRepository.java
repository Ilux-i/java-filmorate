package ru.yandex.practicum.filmorate.dao.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

@Repository
public class DirectorRepository extends BaseRepository<Director> {

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM directors WHERE id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM directors";
    private static final String INSERT_QUERY = "INSERT INTO directors(name) VALUES (?)";
    private static final String UPDATE_QUERY = "UPDATE directors SET name = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM directors WHERE id = ?";

    public DirectorRepository(JdbcTemplate jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper);
    }

    // Получение режиссёра по id
    public Optional<Director> findById(long directorId) {
        return findOne(FIND_BY_ID_QUERY, directorId);
    }

    // Получение списка всех режиссёров
    public List<Director> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    // Добавление режиссёра
    public Director add(Director director) {
        long id = insert(
                INSERT_QUERY,
                director.getName()
        );
        director.setId(id);
        return director;
    }

    // Обновление режиссёра
    public Director update(Director director) {
        update(
                UPDATE_QUERY,
                director.getName(),
                director.getId()
        );
        return director;
    }

    // Удаление режиссёра по id
    public boolean remove(long directorId) {
        return delete(DELETE_QUERY, directorId);
    }
}
