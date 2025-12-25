package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.like.LikeDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LikeMapper {
    public static LikeDto mapToLikeDto(long userId, long filmId) {
        LikeDto dto = new LikeDto();
        dto.setUserId(userId);
        dto.setFilmId(filmId);
        return dto;
    }
}