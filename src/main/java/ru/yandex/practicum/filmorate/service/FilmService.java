package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Service
public class FilmService {

    private static final Logger log = LoggerFactory.getLogger(FilmService.class);
    @Autowired
    private FilmStorage filmStorage;
    @Autowired
    private UserStorage userStorage;

    public Film addLike(long userId, long filmId) {
        Film film = filmStorage.getAllFilms().get(filmId);
        User user = userStorage.getAllUsers().get(userId);

        if (film == null || user == null) {
            throw new ObjectNotFoundException("Объектов с таким id не существует");
        }

        film.getLikes().add(userId);
        log.info("Пользователь с id: {}, поставил лайк на фильм с id: {}", userId, filmId);
        return film;
    }

    public void removeLike(long userId, long filmId) {
        Film film = filmStorage.getAllFilms().get(filmId);
        User user = userStorage.getAllUsers().get(userId);

        if (film == null || user == null) {
            throw new ObjectNotFoundException("Объектов с таким id не существует");
        }

        film.getLikes().remove(userId);
        log.info("Пользователь с id: {}, удалил лайк на фильм с id: {}", userId, filmId);
    }

    public Collection<Film> getPopularFilms(Long count) {
        long actualCount = (count == null || count <= 0) ? 10 : count;

        return filmStorage.getAllFilms().values().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(actualCount)
                .toList();
    }

}
