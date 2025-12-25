package ru.yandex.practicum.filmorate.dao.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.raing.RatingDto;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class RatingRowMapper implements RowMapper<RatingDto> {
    @Override
    public RatingDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        RatingDto dto = new RatingDto();
        dto.setId(rs.getInt("id"));
        dto.setName(rs.getString("name"));
        return dto;
    }
}
