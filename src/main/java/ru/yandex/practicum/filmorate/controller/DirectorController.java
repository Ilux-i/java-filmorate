package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Collection;

@Slf4j
@RestController()
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {

    private final DirectorService directorService;

    // Добавление режиссёра
    @PostMapping
    public Director add(@RequestBody final Director director) {
        return directorService.create(director);
    }

    // Обновление режиссёра
    @PutMapping
    public Director update(@RequestBody final Director director) {
        return directorService.update(director);
    }

    // Получение режиссёра по его id
    @GetMapping("/{directorId}")
    public Director getById(@PathVariable final long directorId) {
        return directorService.getById(directorId);
    }

    // Получение всех режиссёров
    @GetMapping
    public Collection<Director> getAll() {
        return directorService.getAll();
    }

    // Удаление режиссёра по id
    @DeleteMapping("/{directorId}")
    public void delete(@PathVariable final long directorId) {
        directorService.remove(directorId);
    }

}