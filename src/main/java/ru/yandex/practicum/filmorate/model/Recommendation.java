package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class Recommendation {
    private Long userId;
    @Builder.Default
    private Set<Film> recommendedFilms = new HashSet<>();
}