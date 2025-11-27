//package ru.yandex.practicum.filmorate;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import ru.yandex.practicum.filmorate.controller.FilmController;
//import ru.yandex.practicum.filmorate.exception.ValidationException;
//import ru.yandex.practicum.filmorate.model.Film;
//
//import java.time.LocalDate;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class FilmControllerTest {
//
//    private FilmController filmController;
//
//    @BeforeEach
//    void setUp() {
//        filmController = new FilmController();
//    }
//
//    @Test
//    void addFilm_ValidFilm_ShouldAddFilm() {
//        Film film = Film.builder()
//                .name("Film Name")
//                .description("Description")
//                .releaseDate(LocalDate.of(2000, 1, 1))
//                .duration(120)
//                .build();
//
//        Film result = filmController.addFilm(film);
//
//        assertNotNull(result.getId());
//        assertEquals("Film Name", result.getName());
//    }
//
//    @Test
//    void addFilm_EmptyName_ShouldThrowException() {
//        Film film = Film.builder()
//                .name("")
//                .description("Description")
//                .releaseDate(LocalDate.of(2000, 1, 1))
//                .duration(120)
//                .build();
//
//        assertThrows(ValidationException.class, () -> filmController.addFilm(film));
//    }
//
//    @Test
//    void addFilm_LongDescription_ShouldThrowException() {
//        Film film = Film.builder()
//                .name("Name")
//                .description("A".repeat(201))
//                .releaseDate(LocalDate.of(2000, 1, 1))
//                .duration(120)
//                .build();
//
//        assertThrows(ValidationException.class, () -> filmController.addFilm(film));
//    }
//
//    @Test
//    void updateFilm_ValidFilm_ShouldUpdate() {
//        Film film = Film.builder()
//                .name("Original Name")
//                .description("Description")
//                .releaseDate(LocalDate.of(2000, 1, 1))
//                .duration(120)
//                .build();
//        Film addedFilm = filmController.addFilm(film);
//
//        Film updatedFilm = Film.builder()
//                .id(addedFilm.getId())
//                .name("Updated Name")
//                .description("New Description")
//                .releaseDate(LocalDate.of(2000, 1, 1))
//                .duration(150)
//                .build();
//
//        Film result = filmController.updateFilm(updatedFilm);
//
//        assertEquals("Updated Name", result.getName());
//        assertEquals("New Description", result.getDescription());
//    }
//
//    @Test
//    void getAllFilms_AfterAdding_ShouldReturnAll() {
//        Film film1 = Film.builder()
//                .name("Film 1")
//                .description("Description 1")
//                .releaseDate(LocalDate.of(2000, 1, 1))
//                .duration(120)
//                .build();
//        Film film2 = Film.builder()
//                .name("Film 2")
//                .description("Description 2")
//                .releaseDate(LocalDate.of(2000, 1, 1))
//                .duration(90)
//                .build();
//
//        filmController.addFilm(film1);
//        filmController.addFilm(film2);
//
//        assertEquals(2, filmController.getAllFilms().size());
//    }
//}