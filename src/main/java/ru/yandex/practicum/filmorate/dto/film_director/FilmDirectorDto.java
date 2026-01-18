package ru.yandex.practicum.filmorate.dto.film_director;

import lombok.Data;

// DTO с данными связи фильм-режиссёр
@Data
public class FilmDirectorDto {
    private long id;
    private long filmId;
    private long directorId;
}