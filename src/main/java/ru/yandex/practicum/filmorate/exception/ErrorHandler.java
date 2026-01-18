package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

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
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleObjectNotFoundException(final ObjectNotFoundException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Данные не найдены");
        response.put("message", e);
        response.put("status", HttpStatusCode.valueOf(404));

        return response;
    }

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
