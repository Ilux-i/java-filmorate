package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Feed {
    private Long timestamp;
    private Long userId;
    private EventType eventType; // одно из значениий LIKE, REVIEW или FRIEND
    private Operation operation; // одно из значениий REMOVE, ADD, UPDATE
    private Long eventId;        // primary key
    private Long entityId;       // идентификатор сущности, с которой произошло событие

    public Feed() {
    }

    public Feed(Long timestamp, Long userId, EventType eventType,
                Operation operation, Long eventId, Long entityId) {
        this.timestamp = timestamp;
        this.userId = userId;
        this.eventType = eventType;
        this.operation = operation;
        this.eventId = eventId;
        this.entityId = entityId;
    }
}
