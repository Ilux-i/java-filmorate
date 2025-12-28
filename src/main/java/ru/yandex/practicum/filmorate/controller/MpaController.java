package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;

@Slf4j
@RestController()
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {

    private final MpaService mpaService;

    @GetMapping("/{id}")
    public Mpa getMpa(@PathVariable final long id) {
        return mpaService.getMpa(id);
    }

    @GetMapping
    public Collection<Mpa> getAllMpa() {
        return mpaService.getAll();
    }

}
