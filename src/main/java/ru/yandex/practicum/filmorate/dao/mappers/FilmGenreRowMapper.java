package ru.yandex.practicum.filmorate.dao.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.dto.film_genre.FilmGenreDto;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FilmGenreRowMapper implements RowMapper<FilmGenreDto> {
    @Override
    public FilmGenreDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        FilmGenreDto filmGenreDto = new FilmGenreDto();
        filmGenreDto.setId(rs.getInt("id"));
        filmGenreDto.setFilmId(rs.getInt("film_id"));
        filmGenreDto.setGenre(Genre.valueOf(rs.getString("genre")));
        return filmGenreDto;
    }
}
