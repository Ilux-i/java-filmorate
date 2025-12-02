package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private static HashMap<Long, Film> films = new HashMap<>();
    private static long idCounter = 1;

    public Film addFilm(final Film film) {
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
    }

    public Film updateFilm(final Film film) {
        films.put(film.getId(), film);
        log.info("Film {} updated", film);
        return film;
    }

    public HashMap<Long, Film> getAllFilms() {
        return films;
    }

    private static long getIdCounter() {
        return idCounter++;
    }

}
