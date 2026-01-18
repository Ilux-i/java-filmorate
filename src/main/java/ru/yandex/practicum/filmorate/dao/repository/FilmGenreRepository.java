package ru.yandex.practicum.filmorate.dao.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.mappers.FilmGenreRowMapper;
import ru.yandex.practicum.filmorate.dto.film_genre.FilmGenreDto;
import ru.yandex.practicum.filmorate.mapper.FilmGenreMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class FilmGenreRepository extends BaseRepository<FilmGenreDto> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM film_genre WHERE film_id = ? order by genre_id";
    private static final String INSERT_QUERY = "INSERT INTO film_genre(film_id, genre_id) VALUES (?, ?)";
    private static final String INSERT_GENRES_IN_FILM_QUERY = "INSERT INTO film_genre(film_id, genre_id) VALUES (?, ?)";
    private static final String REMOVE_FILM_GENRE_QUERY = "DELETE FROM film_genre WHERE film_id = ? AND genre_id = ?";
    private static final String REMOVE_FILM_GENRES_QUERY = "DELETE FROM film_genre WHERE film_id = ? AND genre_id IN (%s)";

    public FilmGenreRepository(JdbcTemplate jdbc, FilmGenreRowMapper mapper) {
        super(jdbc, mapper);
    }

    // Получение списка FilmGenreDto в порядке возрастания genreId
    public List<FilmGenreDto> findAllByFilm(long filmId) {
        return findMany(FIND_ALL_QUERY, filmId);
    }

    // Добавление связи жанр-фильм
    public FilmGenreDto add(FilmGenreDto dto) {
        long id = insert(
                INSERT_QUERY,
                dto.getFilmId(),
                dto.getGenreId()
        );
        dto.setId(id);
        return dto;
    }

    // Добавление списка жанров к фильму по из id
    public List<FilmGenreDto> addGenresToFilm(long filmId, List<Long> genreIds) {
        if (genreIds == null || genreIds.isEmpty()) {
            return Collections.emptyList();
        }

        // Подготовка данных к сохранению в бд
        List<Object[]> batchArgs = genreIds.stream()
                .map(genreId -> new Object[]{filmId, genreId})
                .collect(Collectors.toList());

        // Сохранение данных
        jdbc.batchUpdate(INSERT_GENRES_IN_FILM_QUERY, batchArgs);

        // Возвращение списка FilmGenreDto к фильму
        return genreIds.stream()
                .map(genreId ->
                        FilmGenreMapper.mapToFilmGenreDto(filmId, genreId)
                )
                .collect(Collectors.toList());
    }

    // Удаление связи жанр-фильм
    public boolean remove(long filmId, long genreId) {
        return jdbc.update(REMOVE_FILM_GENRE_QUERY, filmId, genreId) > 0;
    }

    // Удаление списка жанров из фильма
    public boolean removeGenres(long filmId, List<Long> genreIds) {
        if (genreIds == null || genreIds.isEmpty()) {
            return false;
        }

        // Формирование строки на удаление
        String placeholders = String.join(",",
                Collections.nCopies(genreIds.size(), "?"));

        // Формирование запроса
        String sql = String.format(
                REMOVE_FILM_GENRES_QUERY,
                placeholders
        );

        //Формирование данных на удаление
        List<Object> params = new ArrayList<>();
        params.add(filmId);
        params.addAll(genreIds);

        // Объединение всего и ответ об успешности операции
        return jdbc.update(sql, params.toArray()) > 0;
    }

}
