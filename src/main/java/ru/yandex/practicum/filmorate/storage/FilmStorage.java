package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    Film addFilm(final Film film);

    Film updateFilm(final Film film);

    Collection<Film> getAllFilms();

}
