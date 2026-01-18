package ru.yandex.practicum.filmorate.dto.review;

import lombok.Data;

@Data
public class ReviewUpdateRequest {
    private Long reviewId;
    private String content;
    private Boolean isPositive;
    private Long userId;
}
