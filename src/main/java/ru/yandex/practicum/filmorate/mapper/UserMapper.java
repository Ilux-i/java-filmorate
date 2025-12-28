package ru.yandex.practicum.filmorate.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.model.User;

@UtilityClass
public final class UserMapper {
    public User mapToUser(NewUserRequest request) {
        return User.builder()
                .login(request.getLogin())
                .email(request.getEmail())
                .name(request.getName())
                .birthday(request.getBirthday())
                .build();
    }

    public UpdateUserRequest mapToUpdateUserRequest(User user) {
        UpdateUserRequest result = new UpdateUserRequest();
        result.setLogin(user.getLogin());
        result.setEmail(user.getEmail());
        result.setName(user.getName());
        result.setBirthday(user.getBirthday());
        return result;
    }

    public User updateUserFields(User user, UpdateUserRequest request) {
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