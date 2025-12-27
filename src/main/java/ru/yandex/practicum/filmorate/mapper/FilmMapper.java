package ru.yandex.practicum.filmorate.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;

@UtilityClass
public class FilmMapper {
    public static UpdateFilmRequest mapToUpdateFilmRequest(Film film) {
        UpdateFilmRequest result = new UpdateFilmRequest();
        result.setName(film.getName());
        result.setDescription(film.getDescription());
        result.setReleaseDate(film.getReleaseDate());
        result.setDuration(film.getDuration());
        result.setRating(film.getMpa());
        return result;
    }

    public static Film updateFilmFields(Film film, UpdateFilmRequest request) {
        if (request.hasName()) {
            film.setName(request.getName());
        }
        if (request.hasDescription()) {
            film.setDescription(request.getDescription());
        }
        if (request.hasReleaseDate()) {
            film.setReleaseDate(request.getReleaseDate());
        }
        if (request.hasDuration()) {
            film.setDuration(request.getDuration());
        }
        if (request.hasRating()) {
            film.setMpa(request.getRating());
        }
        return film;
    }
}
