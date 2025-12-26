package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserMapper {
    public static User mapToUser(NewUserRequest request) {
        return User.builder()
                .login(request.getLogin())
                .email(request.getEmail())
                .name(request.getName())
                .birthday(request.getBirthday())
                .build();
    }

    public static UpdateUserRequest mapToUpdateUserRequest(User user) {
        UpdateUserRequest result = new UpdateUserRequest();
        result.setLogin(user.getLogin());
        result.setEmail(user.getEmail());
        result.setName(user.getName());
        result.setBirthday(user.getBirthday());
        return result;
    }

    public static User updateUserFields(User user, UpdateUserRequest request) {
        if (request.hasEmail()) {
            user.setEmail(request.getEmail());
        }
        if (request.hasLogin()) {
            user.setLogin(request.getLogin());
        }
        if (request.hasName()) {
            user.setName(request.getName());
        }
        if (request.hasBirthday()) {
            user.setBirthday(request.getBirthday());
        }

        return user;
    }
}