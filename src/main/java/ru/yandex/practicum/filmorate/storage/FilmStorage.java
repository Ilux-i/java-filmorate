package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.dto.film_genre.FilmGenreDto;
import ru.yandex.practicum.filmorate.dto.like.LikeDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

public interface FilmStorage {

    // Добавление фильма
    Film addFilm(final Film film);

    // Обновление фильма
    Film updateFilm(final Film film);

    // Получение фильма
    Optional<Film> getFilmById(long filmId);

    // Получение всех фильмов
    HashMap<Long, Film> getAllFilms();

    // Удаление фильма
    boolean removeFilm(Film film);

    // Получение списка жанров по фильму
    Set<Genre> getGenresByFilm(long filmId);

    // Добавление жанра к фильму
    FilmGenreDto addGenreInFilm(long filmId, Genre genre);

    // Удаление жанра из фильма
    boolean removeGenreInFilm(long filmId, Genre genre);

    // Поставить лайк
    LikeDto setLike(long userId, long filmId);

    // Получение количества лайков
    long getLikes(long filmId);

    // Удаления лайка
    boolean removeLike(long userId, long filmId);
}
