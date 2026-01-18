package ru.yandex.practicum.filmorate.dao.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.friend.AllFriendDto;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FriendRowMapper implements RowMapper<AllFriendDto> {
    @Override
    public AllFriendDto mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        AllFriendDto dto = new AllFriendDto();
        dto.setUserId(resultSet.getLong("user_id"));  // Изменено с getInt на getLong
        dto.setFriendId(resultSet.getLong("friend_id"));  // Изменено с getInt на getLong
        dto.setStatus(FriendshipStatus.valueOf(resultSet.getString("status")));
        return dto;
    }
}
