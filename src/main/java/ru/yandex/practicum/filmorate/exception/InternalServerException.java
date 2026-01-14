package ru.yandex.practicum.filmorate.exception;

// Внутренняя ошибка сервера
public class InternalServerException extends RuntimeException {
    public InternalServerException(String message) {
        super(message);
    }
}
