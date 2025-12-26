package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.dto.film_genre.FilmGenreDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public interface FilmStorage {

    // Добавление фильма
    Film addFilm(final Film film);

    // Обновление фильма
    Film updateFilm(final Film film);

    // Получение фильма
    Film getFilmById(long filmId);

    // Получение популярных фильмов
    Collection<Film> getPopularFilms(Long count);

    // Получение всех фильмов
    HashMap<Long, Film> getAllFilms();

    // Удаление фильма
    boolean removeFilm(Film film);

    // Получение списка жанров по фильму
    Set<Genre> getGenresByFilm(long filmId);

    // Добавление жанра к фильму
    FilmGenreDto addGenreInFilm(long filmId, long genreId);

    // Удаление жанра из фильма
    boolean removeGenreInFilm(long filmId, long genreId);

    // Поставить лайк
    void setLike(long userId, long filmId);

    // Получение количества лайков
    long getLikes(long filmId);

    // Удаления лайка
    boolean removeLike(long userId, long filmId);

}
