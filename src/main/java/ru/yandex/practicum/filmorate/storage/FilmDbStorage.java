package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.repository.*;
import ru.yandex.practicum.filmorate.dto.film_genre.FilmGenreDto;
import ru.yandex.practicum.filmorate.dto.like.LikeDto;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.DirectorService;
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
    private final FilmDirectorRepository filmDirectorRepository;
    private final DirectorService directorService;

    // Добавление фильма
    @Override
    public Film addFilm(Film film) {
        Film result = filmRepository.add(film);
        if (film.getGenres() != null) {
            film.getGenres().forEach(genre -> addGenreInFilm(result.getId(), genre.getId()));
        }
        if (film.getDirectors() != null) {
            filmDirectorRepository.addDirectorsToFilm(
                    result.getId(),
                    film.getDirectors().stream().map(Director::getId).toList()
            );
        }
        return result;
    }

    // Обновление фильма
    @Override
    public Film updateFilm(Film film) {
        return filmRepository.update(film);
    }

    // Удаление фильма
    @Override
    public void deleteFilm(long id) {
        filmRepository.remove(id);
    }

    // Получение фильма
    @Override
    public Film getFilmById(long filmId) {
        // Проверка на пустоту
        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> new ObjectNotFoundException("User with id " + filmId + " not found"));
        // Заполнение рейтинга
        film.setMpa(mpaService.getMpa(film.getMpa().getId()));
        // Заполнение жанра
        film.setGenres(getGenresByFilm(film.getId()));
        // Заполнение лайков
        likeRepository.countLikesByFilmId(film.getId());
        // Заполнение режиссёрами
        film.setDirectors(directorService.getDirectorsByFilm(film.getId()));
        return film;
    }

    // Получение популярных фильмов
    @Override
    public Collection<Film> getPopularFilms(Long count) {
        return fullFilms(filmRepository.getPopular(count));
    }

    // Получение всех фильмов
    @Override
    public Collection<Film> getAllFilms() {
        return fullFilms(filmRepository.findAll());

    }

    // Получение списка жанров по фильму
    @Override
    public Set<Genre> getGenresByFilm(long filmId) {
        Set<Genre> result = new HashSet<>();
        filmGenreRepository.findAllByFilm(filmId)
                .forEach(dto -> result.add(Genre
                        .builder()
                        .id(dto.getGenreId())
                        .name(genreService.getGenre(dto.getGenreId()).getName())
                        .build()));
        return result;
    }

    // Добавление жанра к фильму
    @Override
    public FilmGenreDto addGenreInFilm(long filmId, long genreId) {
        return filmGenreRepository.add(mapToFilmGenreDto(filmId, genreId));
    }

    // Добавление жанров в фильм
    @Override
    public List<FilmGenreDto> addGenresToFilm(long filmId, List<Long> genreIds) {
        return filmGenreRepository.addGenresToFilm(filmId, genreIds);
    }

    // Удаление жанра из фильма
    @Override
    public boolean removeGenreInFilm(long filmId, long genreId) {
        return filmGenreRepository.remove(filmId, genreId);
    }

    // Удаление жанров из фильма
    @Override
    public boolean removeGenresInFilm(long filmId, List<Long> genresId) {
        return filmGenreRepository.removeGenres(filmId, genresId);
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
        return likeRepository.remove(filmId, userId);
    }

    @Override
    public Collection<Film> getFilmsByDirector(Long directorId, List<String> sortBy) {
        return fullFilms(filmRepository.getFilmsByDirector(directorId, sortBy));
    }

    // Получение общих фильмов
    @Override
    public Collection<Film> getCommonFilms(long userId, long friendId) {
        // Получаем фильмы из репозитория
        return fullFilms(filmRepository.getCommonFilms(userId, friendId));
    }

    // Поиск фильмов по названию
    @Override
    public Collection<Film> searchByTitle(String query) {
        return fullFilms(filmRepository.searchByTitle(query));
    }

    // Поиск фильмов по режиссёру
    @Override
    public Collection<Film> searchByDirector(String query) {
        return fullFilms(filmRepository.searchByDirector(query));
    }

    // Поиск фильмов по режиссёру и названию
    @Override
    public Collection<Film> searchByAll(String query) {
        return fullFilms(filmRepository.searchByAll(query));
    }

    private Collection<Film> fullFilms(Collection<Film> films) {
        return films.
                stream()
                // Заполнение Mpa
                .peek(film -> film.setMpa(mpaService.getMpa(film.getMpa().getId())))
                // Заполнение лайками
                .peek(film -> likeRepository.countLikesByFilmId(film.getId()))
                // Заполнение жанрами
                .peek(film -> film.setGenres(getGenresByFilm(film.getId())))
                // Заполнение режиссёрами
                .peek(film -> film.setDirectors(directorService
                        .getDirectorsByFilm(film.getId())))
                .toList();
    }
}
