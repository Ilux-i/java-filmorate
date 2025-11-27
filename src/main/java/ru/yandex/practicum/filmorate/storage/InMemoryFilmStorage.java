package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.HashMap;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, Month.DECEMBER, 28);
    private static final Integer MAX_LENGTH_DESCRIPTION = 200;

    private static final Logger log = LoggerFactory.getLogger(InMemoryFilmStorage.class);
    private HashMap<Long, Film> films = new HashMap<>();
    private static long idCounter = 1;

    public Film addFilm(final Film film) {
        if (valid(film)) {
            Film newFilm = Film.builder()
                    .id(getIdCounter())
                    .name(film.getName())
                    .description(film.getDescription())
                    .releaseDate(film.getReleaseDate())
                    .duration(film.getDuration())
                    .build();
            films.put(newFilm.getId(), newFilm);
            log.info("Film {} added", newFilm);
            return newFilm;
        } else {
            log.warn("Film {} not valid when added", film);
            throw new ValidationException("Film no valid");
        }
    }

    public Film updateFilm(final Film film) {
        if (film.getId() != null && films.containsKey(film.getId())) {
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
            if (valid(film)) {
                films.put(film.getId(), film);
                log.info("Film {} updated", film);
                return film;
            } else {
                log.warn("Film {} not valid when updated", film);
                throw new ValidationException("Film no valid");
            }
        } else {
            log.info("User does not have an Id");
            throw new IllegalArgumentException("Id is missing");
        }
    }

    public Collection<Film> getAllFilms() {
        return films.values();
    }

    private boolean valid(Film film) {
        return film.getName() != null &&
                !film.getName().isEmpty() &&
                film.getDescription().length() <= MAX_LENGTH_DESCRIPTION &&
                film.getReleaseDate().isAfter(CINEMA_BIRTHDAY) &&
                film.getDuration() > 0;
    }

    private static long getIdCounter() {
        return idCounter++;
    }

}
