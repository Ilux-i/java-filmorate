package ru.yandex.practicum.filmorate.dto.user;

import lombok.Data;

import java.time.LocalDate;

// DTO с данными обновления пользователя
@Data
public class UpdateUserRequest {
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;

    // Проверки на пустоту

    public boolean hasName() {
        return !(name == null || name.isBlank());
    }

    public boolean hasLogin() {
        return !(login == null || login.isBlank());
    }

    public boolean hasEmail() {
        return !(email == null || email.isBlank());
    }

    public boolean hasBirthday() {
        return !(birthday == null);
    }
}
