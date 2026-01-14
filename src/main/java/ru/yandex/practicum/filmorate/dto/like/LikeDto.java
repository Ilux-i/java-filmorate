package ru.yandex.practicum.filmorate.dto.like;

import lombok.Data;

// DTO с данными связи фильм и кто поставил лайк
@Data
public class LikeDto {
    private long id;
    private long filmId;
    private long userId;
}
