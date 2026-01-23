package ru.yandex.practicum.filmorate.dao.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class DirectorRowMapper implements RowMapper<Director> {
    @Override
    public Director mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        String name = resultSet.getString("name");

        // Очистка от пробелов
        if (name != null) {
            name = name.trim();
        } else {
            name = "";
        }

        return Director.builder()
                .id(resultSet.getLong("id"))
                .name(name)
                .build();
    }
}
