package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Feed {
    private Long timestamp;
    private Long userId;
    private EventType eventType; // одно из значениий LIKE, REVIEW или FRIEND
    private Operation operation; // одно из значениий REMOVE, ADD, UPDATE
    private Long eventId;        // primary key
    private Long entityId;       // идентификатор сущности, с которой произошло событие
}
