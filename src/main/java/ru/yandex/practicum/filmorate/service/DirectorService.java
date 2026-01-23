package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.repository.DirectorRepository;
import ru.yandex.practicum.filmorate.dao.repository.FilmDirectorRepository;
import ru.yandex.practicum.filmorate.dto.film_director.FilmDirectorDto;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {

    private final DirectorRepository directorRepository;
    private final FilmDirectorRepository filmDirectorRepository;

    // Добавление режиссёра
    public Director create(final Director director) {
        // Валидация
        if (director.getName() != null) {
            return directorRepository.add(director);
        } else {
            log.warn("User does not have an Name");
            throw new ValidationException("Name is missing");
        }
    }

    // Обновление режиссёра
    public Director update(final Director director) {
        // Валидация
        if (director.getId() != null) {
            if (director.getName() != null) {
                getById(director.getId());
                return directorRepository.update(director);
            } else {
                log.info("User does not have an Name");
                throw new ValidationException("Name is missing");
            }
        } else {
            log.info("User does not have an Id");
            throw new ValidationException("Id is missing");
        }
    }

    // Получение режиссёра по его id
    public Director getById(final long directorId) {
        return directorRepository.findById(directorId)
                .orElseThrow(() -> new ObjectNotFoundException("Director with id " + directorId + " not found"));
    }

    // Получение всех режиссёров
    public Collection<Director> getAll() {
        return directorRepository.findAll();
    }

    // Удаление режиссёра по id
    public void remove(long directorId) {
        getById(directorId);
        try {
            List<FilmDirectorDto> pairs = filmDirectorRepository.findAllByDirector(directorId);
            filmDirectorRepository.removeFilmDirectorByPair(pairs);
            directorRepository.remove(directorId);
        } catch (InternalServerException e) {
            throw new InternalServerException("Ошибка при удалении режиссёра по id = " + directorId);
        }
    }

    // Получение списка режиссёров по фильму
    public Set<Director> getDirectorsByFilm(long filmId) {
        Set<Director> result = new HashSet<>();
        filmDirectorRepository.findAllByFilm(filmId)
                .forEach(dto -> result.add(Director.builder()
                        .id(dto.getDirectorId())
                        .name(getById(dto.getDirectorId()).getName())
                        .build()));
        return result;
    }
}