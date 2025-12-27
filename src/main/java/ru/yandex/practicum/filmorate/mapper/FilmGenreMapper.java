package ru.yandex.practicum.filmorate.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.dto.film_genre.FilmGenreDto;

@UtilityClass
public class FilmGenreMapper {
    public static FilmGenreDto mapToFilmGenreDto(long filmId, long genreId) {
        FilmGenreDto dto = new FilmGenreDto();
        dto.setFilmId(filmId);
        dto.setGenreId(genreId);
        return dto;
    }
}