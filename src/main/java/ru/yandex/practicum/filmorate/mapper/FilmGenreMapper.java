package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.film_genre.FilmGenreDto;
import ru.yandex.practicum.filmorate.dto.like.LikeDto;
import ru.yandex.practicum.filmorate.model.Genre;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FilmGenreMapper {
    public static FilmGenreDto mapToFilmGenreDto(long filmId, Genre genre) {
        FilmGenreDto dto = new FilmGenreDto();
        dto.setFilmId(filmId);
        dto.setGenre(genre);
        return dto;
    }
}