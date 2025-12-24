package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.HashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, Month.DECEMBER, 28);
    private static final Integer MAX_LENGTH_DESCRIPTION = 200;

    @Autowired
    private final FilmStorage filmStorage;
    @Autowired
    private final UserStorage userStorage;

    public Film addFilm(final Film film) {
        if (valid(film)) {
            return filmStorage.addFilm(film);
        } else {
            log.warn("Film {} not valid when added", film);
            throw new ValidationException("Film no valid");
        }
    }

    public Film updateFilm(final Film film) {
        HashMap<Long, Film> films = filmStorage.getAllFilms();
        if (film.getId() != null) {
            if (!films.containsKey(film.getId())) {
                log.warn("Фильм с id: {} не найден", film.getId());
                throw new ObjectNotFoundException("В бд нет такого фильма");
            }
            Film oldFilm = films.get(film.getId());
            if (film.getName() == null) {
                film.setName(oldFilm.getName());
            }
            if (film.getDescription() == null) {
                film.setDescription(oldFilm.getDescription());
            }
            if (film.getReleaseDate() == null) {
                film.setReleaseDate(oldFilm.getReleaseDate());
            }
            if (film.getDuration() == null) {
                film.setDuration(oldFilm.getDuration());
            }
            film.setLikes(oldFilm.getLikes());
            if (valid(film)) {
                return filmStorage.updateFilm(film);
            } else {
                log.warn("Film {} not valid when updated", film);
                throw new ValidationException("Film no valid");
            }
        } else {
            log.info("User does not have an Id");
            throw new ObjectNotFoundException("Id is missing");
        }
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms().values();
    }

    public Film addLike(final long userId, final long filmId) {
        Film film = filmStorage.getAllFilms().get(filmId);
        User user = userStorage.getAllUsers().get(userId);

        if (film == null || user == null) {
            throw new ObjectNotFoundException("Объектов с таким id не существует");
        }

        film.getLikes().add(userId);
        log.info("Пользователь с id: {}, поставил лайк на фильм с id: {}", userId, filmId);
        return film;
    }

    public void removeLike(final long userId, final long filmId) {
        Film film = filmStorage.getAllFilms().get(filmId);
        User user = userStorage.getAllUsers().get(userId);

        if (film == null || user == null) {
            throw new ObjectNotFoundException("Объектов с таким id не существует");
        }

        film.getLikes().remove(userId);
        log.info("Пользователь с id: {}, удалил лайк на фильм с id: {}", userId, filmId);
    }

    public Collection<Film> getPopularFilms(final Long count) {
        long actualCount = (count == null || count <= 0) ? 10 : count;

        return filmStorage.getAllFilms().values().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(actualCount)
                .toList();
    }

    private boolean valid(Film film) {
        return film.getName() != null &&
                !film.getName().isEmpty() &&
                film.getDescription().length() <= MAX_LENGTH_DESCRIPTION &&
                film.getReleaseDate().isAfter(CINEMA_BIRTHDAY) &&
                film.getDuration() > 0;
    }

}
