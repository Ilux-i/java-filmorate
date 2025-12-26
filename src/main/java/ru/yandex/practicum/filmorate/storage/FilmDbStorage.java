package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.repository.FilmGenreRepository;
import ru.yandex.practicum.filmorate.dao.repository.FilmRepository;
import ru.yandex.practicum.filmorate.dao.repository.LikeRepository;
import ru.yandex.practicum.filmorate.dto.film_genre.FilmGenreDto;
import ru.yandex.practicum.filmorate.dto.like.LikeDto;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.*;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.mapper.FilmGenreMapper.mapToFilmGenreDto;
import static ru.yandex.practicum.filmorate.mapper.LikeMapper.mapToLikeDto;

@Component("FilmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final FilmRepository filmRepository;
    private final FilmGenreRepository filmGenreRepository;
    private final LikeRepository likeRepository;
    private final MpaService mpaService;
    private final GenreService genreService;

    // Добавление фильма
    @Override
    public Film addFilm(Film film) {
        Film result = filmRepository.add(film);
        if (film.getGenres() != null) {
            film.getGenres().forEach(genre -> addGenreInFilm(result.getId(), genre.getId()));
        }
        return result;
    }

    // Обновление фильма
    @Override
    public Film updateFilm(Film film) {
        return filmRepository.update(film);
    }

    // Получение фильма
    @Override
    public Film getFilmById(long filmId) {
        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> new ObjectNotFoundException("User with id " + filmId + " not found"));
        film.setMpa(mpaService.getMpa(film.getMpa().getId()));
        film.setGenres(filmGenreRepository.findAllByFilm(filmId).stream()
                .map(dto -> genreService.getGenre(dto.getGenreId()))
                .collect(Collectors.toSet())
        );
        film.setLikes(likeRepository.findAllByFilm(filmId).stream()
                .map(LikeDto::getUserId)
                .collect(Collectors.toSet())
        );
        return film;
    }

    // Получение популярных фильмов
    @Override
    public Collection<Film> getPopularFilms(Long count) {
        return filmRepository.getPopular(count);
    }

    // Получение всех фильмов
    @Override
    public HashMap<Long, Film> getAllFilms() {
        HashMap<Long, Film> result = new HashMap<>();
        filmRepository.findAll()
                .stream()
                .peek(film -> film.setGenres(getGenresByFilm(film.getId())))
                .forEach(film -> result.put(film.getId(), film));
        return result;
    }

    // Удаление фильма
    @Override
    public boolean removeFilm(Film film) {
        return filmRepository.remove(film.getId());
    }


    // Получение списка жанров по фильму
    @Override
    public Set<Genre> getGenresByFilm(long filmId) {
        Set<Genre> result = new HashSet<>();
        filmGenreRepository.findAllByFilm(filmId).forEach(dto -> result.add(Genre.builder().id(dto.getGenreId()).build()));
        return result;
    }

    // Добавление жанра к фильму
    @Override
    public FilmGenreDto addGenreInFilm(long filmId, long genreId) {
        return filmGenreRepository.add(mapToFilmGenreDto(filmId, genreId));
    }

    // Удаление жанра из фильма
    @Override
    public boolean removeGenreInFilm(long filmId, long genreId) {
        return filmGenreRepository.remove(filmId, genreId);
    }


    // Поставить лайк
    @Override
    public void setLike(long userId, long filmId) {
        likeRepository.add(mapToLikeDto(userId, filmId));
    }

    // Получение количества лайков
    @Override
    public long getLikes(long filmId) {
        return likeRepository.countLikesByFilmId(filmId);
    }

    // Удаления лайка
    @Override
    public boolean removeLike(long userId, long filmId) {
        return likeRepository.remove(userId, filmId);
    }

}
