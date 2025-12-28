package ru.yandex.practicum.filmorate.dto.like;

import lombok.Data;

@Data
public class LikeDto {
    private long id;
    private long filmId;
    private long userId;
}
