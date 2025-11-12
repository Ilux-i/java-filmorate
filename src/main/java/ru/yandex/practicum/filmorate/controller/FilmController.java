package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.HashMap;

@RestController()
@RequestMapping("/films")
public class FilmController {

    private final static Logger log = LoggerFactory.getLogger(FilmController.class);
    private static HashMap<Long, Film> films = new HashMap<>();
    private static long idCounter = 0;

    @PostMapping
    public Film addFilm(@RequestBody final Film film) {
        if (valid(film)) {
            Film newFilm = Film.builder().id(getIdCounter()).name(film.getName()).description(film.getDescription()).releaseDate(film.getReleaseDate()).duration(film.getDuration()).build();
            films.put(newFilm.getId(), newFilm);
            log.info("Film {} added", newFilm.getId());
            return newFilm;
        } else {
            log.info("Film {} not valid when added", film);
            throw new ValidationException("Film no valid");
        }
    }

    @PutMapping
    public Film updateFilm(@RequestBody final Film film) {
        if (film.getId() != null && films.containsKey(film.getId())) {
            Film oldFilm = films.get(film.getId());
            if (film.getName().isEmpty()) {
                film.setName(oldFilm.getName());
            }
            if (film.getDescription().isEmpty()) {
                film.setDescription(oldFilm.getDescription());
            }
            if (film.getReleaseDate() == null) {
                film.setReleaseDate(oldFilm.getReleaseDate());
            }
            if (film.getDuration() == null) {
                film.setDuration(oldFilm.getDuration());
            }
            if (valid(film)) {
                films.put(film.getId(), film);
                log.info("Film {} updated", film.getId());
                return film;
            } else {
                log.info("Film {} not valid when updated", film);
                throw new ValidationException("Film no valid");
            }
        } else {
            log.info("User does not have an Id");
            throw new IllegalArgumentException("Id is missing");
        }
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    private boolean valid(Film film) {
        return film.getName() != null &&
                !film.getName().isEmpty() &&
                film.getDescription().length() <= 200 &&
                film.getReleaseDate().isAfter(LocalDate.of(1895, Month.DECEMBER, 28)) &&
                film.getDuration().isPositive();
    }

    public static long getIdCounter() {
        return idCounter++;
    }

}
