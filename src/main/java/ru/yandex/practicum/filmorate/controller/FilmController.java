package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.RecommendationService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController()
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    // Добавление фильма
    @PostMapping
    public Film addFilm(@RequestBody final Film film) {
        return filmService.addFilm(film);
    }

    // Обновление фильма
    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        log.info(film.toString());
        return filmService.updateFilm(film);
    }

    // Удаление фильма
    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable long id) {
        filmService.remove(id);
    }

    // Добавление лайка к фильму от пользователя по их id соответственно
    @PutMapping("/{filmId}/like/{userId}")
    public Film addLike(@PathVariable final long filmId, @PathVariable final long userId) {
        return filmService.addLike(userId, filmId);
    }

    // Получение фильма по его id
    @GetMapping("/{filmId}")
    public Film getFilm(@PathVariable final long filmId) {
        return filmService.getFilmById(filmId);
    }

    // Получение всех фильмов(в случайном порядке)
    @GetMapping
    public Collection<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    // Получение популярных фильмов по жанру и/или году
    @GetMapping("/popular")
    public Collection<Film> getPopularFilmsByGenreAndYear(@RequestParam(required = false) Long genreId,
                                                          @RequestParam(required = false) Integer year,
                                                          @RequestParam(defaultValue = "10") Long count) {
        return filmService.getPopularFilms(genreId, year, count);
    }

    // Получение фильмов по режиссёру
    @GetMapping("/director/{directorId}")
    public Collection<Film> getFilmsByDirector(@PathVariable Long directorId,
                                               @RequestParam(required = false) List<String> sortBy) {
        return filmService.getFilmsByDirector(directorId, sortBy);
    }

    // Удаление лайка к фильму
    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable final long id, @PathVariable final long userId) {
        filmService.removeLike(userId, id);
    }

    // Получение общих фильмов
    @GetMapping("/common")
    public Collection<Film> getCommonFilms(@RequestParam final long userId, @RequestParam final long friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }

    @GetMapping("/search")
    public Collection<Film> searchFilms(
            @RequestParam("query") String query,
            @RequestParam(value = "by", required = false, defaultValue = "title") String by) {
        return filmService.searchFilms(query, by);
    }
}