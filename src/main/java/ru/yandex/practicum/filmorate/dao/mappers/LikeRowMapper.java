package ru.yandex.practicum.filmorate.dao.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.dto.like.LikeDto;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LikeRowMapper implements RowMapper<LikeDto> {
    @Override
    public LikeDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        LikeDto dto = new LikeDto();
        dto.setId(rs.getInt("id"));
        dto.setFilmId(rs.getInt("film_id"));
        dto.setUserId(rs.getInt("user_id"));
        return dto;
    }
}
