package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.repository.FeedRepository;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component("FeedDbStorage")
@RequiredArgsConstructor
public class FeedDBStorage {
    private final FeedRepository feedRepository;

    public void addFeed(Long userId, EventType eventType, Operation operation, Long entityId) {
        Feed feed = new Feed();
        if (feed.getTimestamp() == null) {
            feed.setTimestamp(System.currentTimeMillis());
        }
        feed.setUserId(userId);
        feed.setEventType(eventType);
        feed.setOperation(operation);
        feed.setEntityId(entityId);
        feedRepository.add(feed);
    }

    public Collection<Feed> getFeed(Long userId) {
        return feedRepository.findByUserId(userId);
    }
}
