package ru.yandex.practicum.filmorate.dto.film_genre;

import lombok.Data;

// DTO с данными связи фильм-жанр
@Data
public class FilmGenreDto {
    private long id;
    private long filmId;
    private long genreId;
}
