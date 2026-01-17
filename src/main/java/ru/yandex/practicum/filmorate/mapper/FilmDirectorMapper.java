package ru.yandex.practicum.filmorate.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.dto.film_director.FilmDirectorDto;

@UtilityClass
public class FilmDirectorMapper {
    public static FilmDirectorDto mapToFilmDirectorDto(long filmId, long directorId) {
        FilmDirectorDto dto = new FilmDirectorDto();
        dto.setFilmId(filmId);
        dto.setDirectorId(directorId);
        return dto;
    }
}
