package ru.yandex.practicum.filmorate.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.dto.friend.AllFriendDto;
import ru.yandex.practicum.filmorate.dto.friend.PairFriendDto;

@UtilityClass
public class FriendMapper {
    public static PairFriendDto mapToUserPairFriendDto(long userId, long friendId) {
        PairFriendDto dto = new PairFriendDto();
        dto.setUserId(userId);
        dto.setFriendId(friendId);
        return dto;
    }

    public static AllFriendDto mapToAllFriendDto(long userId, long friendId) {
        AllFriendDto dto = new AllFriendDto();
        dto.setUserId(userId);
        dto.setFriendId(friendId);
        return dto;
    }
}