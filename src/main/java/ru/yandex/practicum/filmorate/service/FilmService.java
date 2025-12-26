package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.repository.RatingRepository;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.mapper.FilmMapper.mapToUpdateFilmRequest;
import static ru.yandex.practicum.filmorate.mapper.FilmMapper.updateFilmFields;

// добавить удаление фильмов
@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, Month.DECEMBER, 28);
    private static final Integer MAX_LENGTH_DESCRIPTION = 200;
    private static final Integer COUNT_GENRES = 6;
    private static final Integer COUNT_MPA = 5;


    @Autowired
    @Qualifier("FilmDbStorage")
    private final FilmStorage filmStorage;
    @Autowired
    @Qualifier("UserDbStorage")
    private final UserStorage userStorage;
    @Autowired
    private RatingRepository ratingRepository;

    public Film addFilm(final Film film) {
        if (valid(film)) {
            return filmStorage.addFilm(film);
        } else {
            log.warn("Film {} not valid when added", film);
            throw new ValidationException("Film no valid");
        }
    }

    public Film updateFilm(final Film film) {
        if (film.getId() != null) {
            Film oldFilm = filmStorage.getFilmById(film.getId());
            UpdateFilmRequest updateFilm = mapToUpdateFilmRequest(film);
            Film result = updateFilmFields(oldFilm, updateFilm);
            if (valid(result)) {
                if (!result.getGenres().isEmpty()) {
                    updateGenres(result.getId(), result.getGenres());
                }
                return filmStorage.updateFilm(result);
            } else {
                log.warn("Film {} not valid when updated", result);
                throw new ValidationException("Film no valid");
            }
        } else {
            log.info("User does not have an Id");
            throw new ObjectNotFoundException("Id is missing");
        }
    }

    public Film getFilmById(final long id) {
        return filmStorage.getFilmById(id);
    }

    public Collection<Film> getPopularFilms(Long count) {
        return filmStorage.getPopularFilms(count);
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms().values();
    }

    public Film addLike(final long userId, final long filmId) {
        Film film = filmStorage.getFilmById(filmId);
        userStorage.getUserById(userId);
        filmStorage.setLike(userId, filmId);
        log.info("Пользователь с id: {}, поставил лайк на фильм с id: {}", userId, filmId);
        return film;
    }

    public void removeLike(final long userId, final long filmId) {
        filmStorage.getFilmById(filmId);
        userStorage.getUserById(userId);

        if (filmStorage.removeLike(userId, filmId)) {
            log.info("Пользователь с id: {}, удалил лайк на фильм с id: {}", userId, filmId);
        } else {
            log.info("Пользователь с id: {}, не ставил лайк на фильм с id: {}", userId, filmId);
        }
    }

    public long getCountLikes(Film film) {
        return filmStorage.getLikes(film.getId());
    }

    public void addGenreInFilm(long filmId, long genreId) {
        try {
            filmStorage.addGenreInFilm(filmId, genreId);
        } catch (IllegalArgumentException e) {
            log.warn("Жанра '{}' не существует", genreId);
        }
    }

    private void updateGenres(long filmId, Set<Genre> genres) {
        Set<Genre> oldGenres = filmStorage.getGenresByFilm(filmId);

        Set<Long> toRemove = oldGenres.stream()
                .filter(genre -> !genres.contains(genre))
                .map(Genre::getId)
                .collect(Collectors.toSet());

        Set<Long> toAdd = genres.stream()
                .filter(genre -> !oldGenres.contains(genre))
                .map(Genre::getId)
                .collect(Collectors.toSet());

        toRemove.forEach(genre -> filmStorage.removeGenreInFilm(filmId, genre));
        toAdd.forEach(genre -> filmStorage.addGenreInFilm(filmId, genre));
    }

    private boolean valid(Film film) {
        if (film.getMpa() != null && film.getMpa().getId() > COUNT_MPA) {
            throw new ObjectNotFoundException("Не найден рейтинг");
        }
        if (film.getGenres() != null && !film.getGenres().stream().filter(genre -> genre.getId() > COUNT_GENRES).toList().isEmpty()) {
            throw new ObjectNotFoundException("Не найден жанр,");
        }
        return film.getName() != null &&
                !film.getName().isEmpty() &&
                film.getDescription().length() <= MAX_LENGTH_DESCRIPTION &&
                film.getReleaseDate().isAfter(CINEMA_BIRTHDAY) &&
                film.getDuration() > 0;
    }

}
