package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.repository.RatingRepository;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;


@Slf4j
@Service
@RequiredArgsConstructor
public class MpaService {

    @Autowired
    private RatingRepository ratingRepository;

    public Mpa getMpa(long id) {
        return ratingRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("В бд нет такого рейтинга"));
    }

    public Collection<Mpa> getAll() {
        return ratingRepository.findAll();
    }

}

