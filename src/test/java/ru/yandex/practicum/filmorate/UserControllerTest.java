package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController();
    }

    @Test
    void addUser_ValidUser_ShouldAddUser() {
        User user = User.builder()
                .email("test@mail.com")
                .login("login")
                .name("Name")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        User result = userController.addUser(user);

        assertNotNull(result.getId());
        assertEquals("test@mail.com", result.getEmail());
    }

    @Test
    void addUser_EmptyEmail_ShouldThrowException() {
        User user = User.builder()
                .email("")
                .login("login")
                .name("Name")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        assertThrows(ValidationException.class, () -> userController.addUser(user));
    }

    @Test
    void addUser_InvalidEmail_ShouldThrowException() {
        User user = User.builder()
                .email("invalid-email")
                .login("login")
                .name("Name")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        assertThrows(ValidationException.class, () -> userController.addUser(user));
    }

    @Test
    void addUser_EmptyLogin_ShouldThrowException() {
        User user = User.builder()
                .email("test@mail.com")
                .login("")
                .name("Name")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        assertThrows(ValidationException.class, () -> userController.addUser(user));
    }

    @Test
    void addUser_LoginWithSpaces_ShouldThrowException() {
        User user = User.builder()
                .email("test@mail.com")
                .login("log in")
                .name("Name")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        assertThrows(ValidationException.class, () -> userController.addUser(user));
    }

    @Test
    void addUser_EmptyName_ShouldUseLoginAsName() {
        User user = User.builder()
                .email("test@mail.com")
                .login("login")
                .name("")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        User result = userController.addUser(user);

        assertEquals("login", result.getName());
    }

    @Test
    void updateUser_ValidUser_ShouldUpdate() {
        User user = User.builder()
                .email("original@mail.com")
                .login("original")
                .name("Original Name")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        User addedUser = userController.addUser(user);

        User updatedUser = User.builder()
                .id(addedUser.getId())
                .email("updated@mail.com")
                .login("updated")
                .name("Updated Name")
                .birthday(LocalDate.of(1995, 1, 1))
                .build();

        User result = userController.updateUser(updatedUser);

        assertEquals("updated@mail.com", result.getEmail());
        assertEquals("updated", result.getLogin());
    }

    @Test
    void getAllUsers_AfterAdding_ShouldReturnAll() {
        User user1 = User.builder()
                .email("user1@mail.com")
                .login("user1")
                .name("User One")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        User user2 = User.builder()
                .email("user2@mail.com")
                .login("user2")
                .name("User Two")
                .birthday(LocalDate.of(1995, 1, 1))
                .build();

        userController.addUser(user1);
        userController.addUser(user2);

        assertEquals(2, userController.getAllUsers().size());
    }
}