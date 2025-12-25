package ru.yandex.practicum.filmorate.dao.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.film_genre.FilmGenreDto;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FilmGenreRowMapper implements RowMapper<FilmGenreDto> {
    @Override
    public FilmGenreDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        FilmGenreDto filmGenreDto = new FilmGenreDto();
        filmGenreDto.setId(rs.getInt("id"));
        filmGenreDto.setFilmId(rs.getInt("film_id"));
        filmGenreDto.setGenreId(rs.getInt("genre_id"));
        return filmGenreDto;
    }
}
