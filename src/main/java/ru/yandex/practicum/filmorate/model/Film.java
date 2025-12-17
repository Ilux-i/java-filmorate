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
    private Rating rating;

    @Builder.Default
    private Set<Long> likes = new HashSet<>();

    @Builder.Default
    private Set<Genre> genres = new HashSet<>();
}

enum Rating {
    G,
    PG,
    PG_13,
    R,
    NC_17
}

enum Genre {
    Comedy,
    Drama,
    Animation,
    Thriller,
    Documentary,
    Action
}
