package ru.yandex.practicum.filmorate.dto.film_genre;

import lombok.Data;

@Data
public class FilmGenreDto {
    private long id;
    private long filmId;
    private long genreId;
}
