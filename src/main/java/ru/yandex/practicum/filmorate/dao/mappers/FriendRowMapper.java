package ru.yandex.practicum.filmorate.dao.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.friend.FriendDto;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FriendRowMapper implements RowMapper<FriendDto> {
    @Override
    public FriendDto mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        FriendDto dto = new FriendDto();
        dto.setId(resultSet.getInt("friend_id"));
        dto.setStatus(FriendshipStatus.valueOf(resultSet.getString("status")));
        return dto;
    }
}

