package ru.yandex.practicum.filmorate.dao.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.film_director.FilmDirectorDto;
import ru.yandex.practicum.filmorate.mapper.FilmDirectorMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class FilmDirectorRepository extends BaseRepository<FilmDirectorDto> {

    private static final String FIND_ALL_DIRECTORS_QUERY = "SELECT * FROM film_director WHERE film_id = ? order by director_id";
    private static final String FIND_ALL_FILMS_QUERY = "SELECT * FROM film_director WHERE director_id = ? order by film_id";
    private static final String INSERT_QUERY = "INSERT INTO film_director(film_id, director_id) VALUES (?, ?)";
    private static final String INSERT_DIRECTORS_IN_FILM_QUERY = "INSERT INTO film_director(film_id, director_id) VALUES (?, ?)";
    private static final String REMOVE_FILM_DIRECTOR_QUERY = "DELETE FROM film_director WHERE film_id = ? AND director_id = ?";
    private static final String DELETE_BY_LIST_QUERY = "DELETE FROM friends WHERE ";

    public FilmDirectorRepository(JdbcTemplate jdbc, RowMapper<FilmDirectorDto> mapper) {
        super(jdbc, mapper);
    }

    // Получение списка FilmDirectorDto в порядке возрастания directorId
    public List<FilmDirectorDto> findAllByFilm(long filmId) {
        return findMany(FIND_ALL_DIRECTORS_QUERY, filmId);
    }

    // Получение списка FilmDirectorDto в порядке возрастания filmId
    public List<FilmDirectorDto> findAllByDirector(long directorId) {
        return findMany(FIND_ALL_FILMS_QUERY, directorId);
    }

    // Добавление связи фильм-режиссёр
    public FilmDirectorDto add(FilmDirectorDto dto) {
        long id = insert(
                INSERT_QUERY,
                dto.getFilmId(),
                dto.getDirectorId()
        );
        dto.setId(id);
        return dto;
    }

    // Добавление списка режиссёров к фильму по id
    public List<FilmDirectorDto> addDirectorsToFilm(long filmId, List<Long> directorIds) {
        if (directorIds == null || directorIds.isEmpty()) {
            return Collections.emptyList();
        }

        // Подготовка данных к сохранению в бд
        List<Object[]> batchArgs = directorIds.stream()
                .map(genreId -> new Object[]{filmId, genreId})
                .collect(Collectors.toList());

        // Сохранение данных
        jdbc.batchUpdate(INSERT_DIRECTORS_IN_FILM_QUERY, batchArgs);

        // Возвращение списка FilmGenreDto к фильму
        return directorIds.stream()
                .map(directorId ->
                        FilmDirectorMapper.mapToFilmDirectorDto(filmId, directorId)
                )
                .collect(Collectors.toList());
    }

    // Удаление связи фильм-режиссёр
    public boolean remove(long filmId, long directorId) {
        return jdbc.update(REMOVE_FILM_DIRECTOR_QUERY, filmId, directorId) > 0;
    }

    // Удаление связей фильм-режиссёр
    public void removeFilmDirectorByPair(List<FilmDirectorDto> pairs) {
        List<String> conditions = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        for (FilmDirectorDto pair : pairs) {
            conditions.add("(user_id = ? AND friend_id = ?)");
            params.add(pair.getFilmId());
            params.add(pair.getDirectorId());
        }
        String whereClause = String.join(" OR ", conditions);

        update(DELETE_BY_LIST_QUERY + whereClause, params.toArray());
    }
}
