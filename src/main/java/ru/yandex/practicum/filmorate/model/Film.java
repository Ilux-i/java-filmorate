package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class Film {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private Mpa mpa;

    @Builder.Default
    private Set<Long> likes = new HashSet<>();

    @Builder.Default
    private Set<Genre> genres = new HashSet<>();

    @Builder.Default
    private Set<Long> directors = new HashSet<>();
}

