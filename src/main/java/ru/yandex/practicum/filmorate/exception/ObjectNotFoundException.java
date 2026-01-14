package ru.yandex.practicum.filmorate.exception;

// Не найден объект
public class ObjectNotFoundException extends RuntimeException {
    public ObjectNotFoundException(String message) {
        super(message);
    }
}
