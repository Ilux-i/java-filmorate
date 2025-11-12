package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class Film {
    Long id;
    String name;
    String description;
    LocalDate releaseDate;
    Integer duration;
}
