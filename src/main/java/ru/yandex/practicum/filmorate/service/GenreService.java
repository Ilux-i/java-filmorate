package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.repository.GenreRepository;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {

    @Autowired
    private GenreRepository genreRepository;

    public Genre getGenre(long id) {
        return genreRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("В бд нет такого жанра"));
    }

    public Collection<Genre> getAll() {
        return genreRepository.findAll();
    }

}
