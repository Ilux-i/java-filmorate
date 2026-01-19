package ru.yandex.practicum.filmorate.dto.statistics;

import lombok.Data;

@Data
public class GenreYearStatistic {
    private Integer year;
    private Long genreId;
    private Long likeCount;
    private Long filmCount;
}