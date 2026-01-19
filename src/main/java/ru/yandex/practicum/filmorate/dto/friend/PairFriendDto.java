package ru.yandex.practicum.filmorate.dto.friend;

import lombok.Data;

@Data
public class PairFriendDto {
    private Long userId;  // Изменено с long на Long
    private Long friendId;
}