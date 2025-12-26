package ru.yandex.practicum.filmorate.dto.friend;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;

@Data
public class AllFriendDto {
    private long userId;
    private long friendId;
    private FriendshipStatus status;
}
