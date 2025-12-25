package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = {"/schema.sql", "/test-data.sql"}) // Добавляем скрипты инициализации
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserDbStorageTest {
	private final UserDbStorage userStorage;

	@Test
	public void testFindUserById() {
		// Сначала создаем пользователя для теста
		User testUser = User.builder()
				.email("test@example.com")
				.login("testlogin")
				.name("Test User")
				.birthday(LocalDate.of(1990, 1, 1))
				.build();

		User savedUser = userStorage.addUser(testUser);
		Long userId = savedUser.getId();

		// Теперь ищем пользователя
		Optional<User> userOptional = userStorage.getUserById(userId);

		// Проверяем
		assertThat(userOptional)
				.isPresent()
				.hasValueSatisfying(user -> {
					assertThat(user.getId()).isEqualTo(userId);
					assertThat(user.getEmail()).isEqualTo("test@example.com");
					assertThat(user.getLogin()).isEqualTo("testlogin");
					assertThat(user.getName()).isEqualTo("Test User");
				});
	}

	@Test
	public void testFindUserById_NotFound() {
		// Ищем несуществующего пользователя
		Optional<User> userOptional = userStorage.getUserById(999L);

		// Проверяем, что Optional пустой
		assertThat(userOptional).isEmpty();
	}
}