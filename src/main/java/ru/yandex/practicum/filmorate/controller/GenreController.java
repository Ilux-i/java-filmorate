package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

@Slf4j
@RestController()
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    // Получение жанра по его id
    @GetMapping("/{genreId}")
    public Genre getGenre(@PathVariable final long genreId) {
        return genreService.getGenre(genreId);
    }

    // Получение всех жанров
    @GetMapping
    public Collection<Genre> getAllGenre() {
        return genreService.getAll();
    }

}
