package ru.yandex.practicum.filmorate.dao.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class GenreRowMapper implements RowMapper<GenreDto> {
    @Override
    public GenreDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        GenreDto dto = new GenreDto();
        dto.setId(rs.getInt("id"));
        dto.setName(rs.getString("name"));
        return dto;
    }
}
