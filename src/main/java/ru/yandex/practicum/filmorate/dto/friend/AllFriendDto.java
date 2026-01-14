package ru.yandex.practicum.filmorate.dto.friend;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;

// DTO с данными связи пользователь-друг и их статус
@Data
public class AllFriendDto {
    private long userId;
    private long friendId;
    private FriendshipStatus status;
}
