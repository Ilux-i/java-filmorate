package ru.yandex.practicum.filmorate.dao.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.film_director.FilmDirectorDto;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FilmDirectorRowMapper implements RowMapper<FilmDirectorDto> {
    @Override
    public FilmDirectorDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        FilmDirectorDto filmDirectorDto = new FilmDirectorDto();
        filmDirectorDto.setId(rs.getInt("id"));
        filmDirectorDto.setFilmId(rs.getInt("film_id"));
        filmDirectorDto.setDirectorId(rs.getInt("director_id"));
        return filmDirectorDto;
    }
}
