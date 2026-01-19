package ru.yandex.practicum.filmorate.dto.review;

import lombok.Data;

@Data
public class ReviewRequest {
    private String content;
    private Boolean isPositive;
    private Long userId;
    private Long filmId;
}
