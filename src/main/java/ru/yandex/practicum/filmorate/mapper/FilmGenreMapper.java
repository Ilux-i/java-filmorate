package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.film_genre.FilmGenreDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FilmGenreMapper {
    public static FilmGenreDto mapToFilmGenreDto(long filmId, long genreId) {
        FilmGenreDto dto = new FilmGenreDto();
        dto.setFilmId(filmId);
        dto.setGenreId(genreId);
        return dto;
    }
}