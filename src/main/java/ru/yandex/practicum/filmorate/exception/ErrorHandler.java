package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    public ErrorResponse handleValidationException(final ValidationException e) {
        return ErrorResponse.create(
                e,
                HttpStatusCode.valueOf(400),
                "Ошибка валидации данных: " + e.getMessage()
        );
    }

    @ExceptionHandler
    public ErrorResponse handleObjectNotFoundException(final ObjectNotFoundException e) {
        return ErrorResponse.create(
                e,
                HttpStatusCode.valueOf(404),
                "Данные не найдены"
        );
    }

    // Добавить обработку InternalServerException

    @ExceptionHandler
    public ErrorResponse handleOtherException(final Throwable e) {
        log.warn(e.getMessage(), e);
        return ErrorResponse.create(
                e,
                HttpStatusCode.valueOf(500),
                "Непредвиденная ошибка"
        );
    }

}
