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

    // Получение жанра по id
    public Genre getGenre(long genreId) {
        return genreRepository.findById(genreId)
                .orElseThrow(() -> new ObjectNotFoundException("В бд нет такого жанра"));
    }

    // Получение всех возможных жанров
    public Collection<Genre> getAll() {
        return genreRepository.findAll();
    }

}
