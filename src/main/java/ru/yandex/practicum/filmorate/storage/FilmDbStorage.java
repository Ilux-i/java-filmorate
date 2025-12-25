package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.repository.FilmGenreRepository;
import ru.yandex.practicum.filmorate.dao.repository.FilmRepository;
import ru.yandex.practicum.filmorate.dao.repository.LikeRepository;
import ru.yandex.practicum.filmorate.dto.film_genre.FilmGenreDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static ru.yandex.practicum.filmorate.mapper.FilmGenreMapper.mapToFilmGenreDto;
import static ru.yandex.practicum.filmorate.mapper.LikeMapper.mapToLikeDto;

@Component("FilmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final FilmRepository filmRepository;
    private final FilmGenreRepository filmGenreRepository;
    private final LikeRepository likeRepository;

    // Добавление фильма
    @Override
    public Film addFilm(Film film) {
        film.getGenres()
                .forEach(genreId -> removeGenreInFilm(film.getId(), genreId));
        return filmRepository.add(film);
    }

    // Обновление фильма
    @Override
    public Film updateFilm(Film film) {
        return filmRepository.update(film);
    }

    // Получение фильма
    @Override
    public Optional<Film> getFilmById(long filmId) {
        return filmRepository.findById(filmId);
    }

    // Получение всех фильмов
    @Override
    public HashMap<Long, Film> getAllFilms() {
        HashMap<Long, Film> result = new HashMap<>();
        filmRepository.findAll()
                .stream()
                .peek(film -> film.setGenres(getGenresByFilm(film.getId())))
                .forEach(film -> result.put(film.getId(), film));
        return result;
    }

    // Удаление фильма
    @Override
    public boolean removeFilm(Film film) {
        return filmRepository.remove(film.getId());
    }


    // Получение списка жанров по фильму
    @Override
    public Set<Long> getGenresByFilm(long filmId) {
        Set<Long> result = new HashSet<>();
        filmGenreRepository.findAllByFilm(filmId).forEach(dto -> result.add(dto.getGenreId()));
        return result;
    }

    // Добавление жанра к фильму
    @Override
    public FilmGenreDto addGenreInFilm(long filmId, long genreId) {
        return filmGenreRepository.add(mapToFilmGenreDto(filmId, genreId));
    }

    // Удаление жанра из фильма
    @Override
    public boolean removeGenreInFilm(long filmId, long genreId) {
        return filmGenreRepository.remove(filmId, genreId);
    }


    // Поставить лайк
    @Override
    public void setLike(long userId, long filmId){
        likeRepository.add(mapToLikeDto(userId, filmId));
    }

    // Получение количества лайков
    @Override
    public long getLikes(long filmId){
        return likeRepository.countLikesByFilmId(filmId);
    }

    // Удаления лайка
    @Override
    public boolean removeLike(long userId, long filmId){
        return likeRepository.remove(userId, filmId);
    }

}
