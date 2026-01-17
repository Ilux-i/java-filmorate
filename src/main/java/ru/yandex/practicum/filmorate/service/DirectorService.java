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
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {

    private final DirectorRepository directorRepository;
    private final FilmDirectorRepository filmDirectorRepository;

    // Добавление режиссёра
    public Director create(final Director director) {
        // Проверка на валидацию
        if (director != null) {
            return directorRepository.add(director);
        } else {
            log.warn("Film {} not valid when added", director);
            throw new ValidationException("Film no valid");
        }
    }

    // Обновление режиссёра
    public Director update(final Director director) {
        if (director.getId() != null) {
            if(director.getName() != null) {
                return directorRepository.update(director);
            } else {
                return null;
            }
        } else {
            log.info("User does not have an Id");
            throw new ObjectNotFoundException("Id is missing");
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
        try {
            List<FilmDirectorDto> pairs = filmDirectorRepository.findAllByDirector(directorId);
            filmDirectorRepository.removeFilmDirectorByPair(pairs);
            directorRepository.remove(directorId);
        } catch (InternalServerException e) {
            throw new InternalServerException("Ошибка при удалении режиссёра по id = " + directorId);
        }
    }

}
