package ru.yandex.practicum.filmorate.dto.friend;

import lombok.Data;

// DTO с данными связи пользователь-друг без статуса
@Data
public class PairFriendDto {
    private long userId;
    private long friendId;
}
