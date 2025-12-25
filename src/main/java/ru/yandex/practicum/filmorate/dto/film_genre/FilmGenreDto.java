package ru.yandex.practicum.filmorate.dto.film_genre;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.Genre;

@Data
public class FilmGenreDto {
    private long id;
    private long filmId;
    private Genre genre;
}
