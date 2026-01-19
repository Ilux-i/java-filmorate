package ru.yandex.practicum.filmorate.dto.friend;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;

@Data
public class AllFriendDto {
    private Long userId;  // Изменено с long на Long
    private Long friendId;
    private FriendshipStatus status;
}