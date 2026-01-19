package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.repository.DirectorRepository;
import ru.yandex.practicum.filmorate.dao.repository.FilmDirectorRepository;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.dto.film_director.FilmDirectorDto;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.mapper.FilmMapper.mapToUpdateFilmRequest;
import static ru.yandex.practicum.filmorate.mapper.FilmMapper.updateFilmFields;


@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, Month.DECEMBER, 28);
    private static final Integer MAX_LENGTH_DESCRIPTION = 200;
    private static final Integer COUNT_GENRES = 6;
    private static final Integer COUNT_MPA = 5;


    @Qualifier("FilmDbStorage")
    private final FilmStorage filmStorage;
    @Qualifier("UserDbStorage")
    private final UserStorage userStorage;

    private final FilmDirectorRepository filmDirectorRepository;
    private final DirectorRepository directorRepository;

    // Добавление фильма
    public Film addFilm(final Film film) {
        // Проверка на валидацию
        if (valid(film)) {
            return filmStorage.addFilm(film);
        } else {
            log.warn("Film {} not valid when added", film);
            throw new ValidationException("Film no valid");
        }
    }

    // Обновление фильма
    public Film updateFilm(Film film) {
        if (film.getId() != null) {
            // Старые данные фильма
            Film oldFilm = filmStorage.getFilmById(film.getId());
            // Обновлённые данные фильма
            UpdateFilmRequest updateFilm = mapToUpdateFilmRequest(film);
            // Фильм из старых и обновлённый данных
            Film result = updateFilmFields(oldFilm, updateFilm);
            // Валидация
            if (valid(result)) {
                if (film.getGenres() != null) {
                    updateGenres(result.getId(), film.getGenres());
                }
                if (film.getDirectors() != null) {
                    updateDirectors(
                            result.getId(),
                            film.getDirectors().stream().map(Director::getId).collect(Collectors.toSet())
                    );
                    result.setDirectors(film.getDirectors());
//                    result.setDirectors(directorRepository.findAllByList(filmDirectorRepository
//                            .findAllByFilm(result.getId()).stream()
//                                .map(FilmDirectorDto::getDirectorId)
//                                .toList()));
                }
                filmStorage.updateFilm(result);
                return result;
            } else {
                log.warn("Film {} not valid when updated", result);
                throw new ValidationException("Film no valid");
            }
        } else {
            log.info("User does not have an Id");
            throw new ObjectNotFoundException("Id is missing");
        }
    }

    // Удаление фильма
    public void remove(long id) {
        filmDirectorRepository.removeFilmDirectorByPair(filmDirectorRepository
                .findAllByFilm(id)
        );
        filmStorage.deleteFilm(id);
    }

    // Получение фильма по id
    public Film getFilmById(final long id) {
        return filmStorage.getFilmById(id);
    }

    // Получение популярных фильмов
    public Collection<Film> getPopularFilms(Long count) {
        return filmStorage.getPopularFilms(count);
    }

    // Получение всех фильмов
    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms().values();
    }

    // Добавление лайка к фильму
    public Film addLike(final long userId, final long filmId) {
        Film film = filmStorage.getFilmById(filmId);
        userStorage.getUserById(userId);
        filmStorage.setLike(userId, filmId);
        log.info("Пользователь с id: {}, поставил лайк на фильм с id: {}", userId, filmId);
        return film;
    }

    // Удаление лайка к фильму
    public void removeLike(final long userId, final long filmId) {
        filmStorage.getFilmById(filmId);
        userStorage.getUserById(userId);

        if (filmStorage.removeLike(userId, filmId)) {
            log.info("Пользователь с id: {}, удалил лайк на фильм с id: {}", userId, filmId);
        } else {
            log.info("Пользователь с id: {}, не ставил лайк на фильм с id: {}", userId, filmId);
        }
    }

    // Получение количества лайков
    public long getCountLikes(Film film) {
        return filmStorage.getLikes(film.getId());
    }

    // Добавление жанра к фильму
    public void addGenreInFilm(long filmId, long genreId) {
        try {
            filmStorage.addGenreInFilm(filmId, genreId);
        } catch (IllegalArgumentException e) {
            log.warn("Жанра '{}' не существует", genreId);
        }
    }

    // Обновление списка жанров к фильму
    private void updateGenres(long filmId, Set<Genre> genres) {
        // Старые жанры
        Set<Genre> oldGenres = filmStorage.getGenresByFilm(filmId);

        // Жанры, которые надо удалить
        List<Long> toRemove = oldGenres.stream()
                .filter(genre -> !genres.contains(genre))
                .map(Genre::getId)
                .collect(Collectors.toList());

        // Жанры, которые надо добавить
        List<Long> toAdd = genres.stream()
                .filter(genre -> !oldGenres.contains(genre))
                .map(Genre::getId)
                .collect(Collectors.toList());

        filmStorage.removeGenresInFilm(filmId, toRemove);
        filmStorage.addGenresToFilm(filmId, toAdd);
    }

    // Обновление списка режиссёров к фильму
    private void updateDirectors(long filmId, Set<Long> directors) {
        // Старые режиссёры
        List<FilmDirectorDto> oldDirectors = filmDirectorRepository.findAllByFilm(filmId);

        // Режиссёры, которые надо удалить
        List<FilmDirectorDto> toRemove = oldDirectors.stream()
                .filter(pair -> !directors.contains(pair.getDirectorId()))
                .collect(Collectors.toList());

        // Режиссёры, которые надо добавить
        List<Long> toAdd = directors.stream()
                .filter(directorId -> !oldDirectors.contains(directorId))
                .collect(Collectors.toList());

        filmDirectorRepository.removeFilmDirectorByPair(toRemove);
        filmDirectorRepository.addDirectorsToFilm(filmId, toAdd);
    }

    // Валидация
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

    // Получение общих фильмов
    public Collection<Film> getCommonFilms(long userId, long friendId) {
        // Валидация
        if (userId < 1 || friendId < 1) {
            log.warn("Не найден пользователь или друг: {}, {}", userId, friendId);
            throw new ValidationException("Не найден пользователь или друг");
        }
        userStorage.getUserById(userId);
        userStorage.getUserById(friendId);
        List<Film> common = filmStorage.getCommonFilms(userId, friendId);
        log.info("Общие фильмы для пользователей {} и {}: {} штук", userId, friendId, common.size());
        return common;
    }

    public Collection<Film> getFilmsByDirector(Long directorId, List<String> sortBy) {
        return filmStorage.getFilmsByDirector(directorId, sortBy);
    }

    // Поиск фильмов по аргументам
    public Collection<Film> searchFilms(String query, String by) {
        return switch (by) {
            case "title" -> searchByTitle(query);
            case "director" -> searchByDirector(query);
            case "title,director", "director,title" -> searchByDirectorAndTitle(query);
            default -> throw new ValidationException("Нет поиска по " + by + " аргументу");
        };
    }

    // Поиск фильмов по названию
    private Collection<Film> searchByTitle(String query) {
        return filmStorage.searchByTitle(query.toLowerCase());
    }

    // Поиск фильмов по режиссёру
    private Collection<Film> searchByDirector(String query) {
        return filmStorage.searchByDirector(query);
    }

    // Поиск фильмов по режиссёру и названию
    private Collection<Film> searchByDirectorAndTitle(String query) {
        return filmStorage.searchByAll(query);
    }

}
