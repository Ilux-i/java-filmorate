package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.PairFriendDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FriendMapper {
    public static PairFriendDto mapToUserPairFriendDto(long userId, long friendId) {
        PairFriendDto dto = new PairFriendDto();
        dto.setUserId(userId);
        dto.setFriendId(friendId);
        return dto;
    }
}
