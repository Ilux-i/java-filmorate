package ru.yandex.practicum.filmorate.dto.friend;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;

@Data
public class FriendDto {
    private long id;
    FriendshipStatus status;
}
