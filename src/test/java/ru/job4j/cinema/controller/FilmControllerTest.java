package ru.job4j.cinema.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.Genre;
import ru.job4j.cinema.service.film.FilmService;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class FilmControllerTest {

    private MockMvc mockMvc;
    private FilmService filmService;

    @BeforeEach
    void setUp() {
        filmService = mock(FilmService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new FilmController(filmService)).build();
    }

    /**
 * Тестирует сценарий: корректность возвращаемых данных при get all_then return films list view
 * 
 * @see #whenGetAll_thenReturnFilmsListView()
 */


    @Test


    void whenGetAll_thenReturnFilmsListView() throws Exception {
        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(view().name("films/list"))
                .andExpect(model().attributeExists("films"));

        verify(filmService).findAll();
    }

    /**
 * Тестирует сценарий: успешное выполнение при get by id exists_then return one film view
 * 
 * @see #whenGetByIdExists_thenReturnOneFilmView()
 */


    @Test


    void whenGetByIdExists_thenReturnOneFilmView() throws Exception {
        Film film = new Film(1, "Inception", "Dreams", 2010, 1, 16, 148, 1);
        film.setGenre(new Genre(1, "Sci-Fi"));
        when(filmService.findById(1)).thenReturn(Optional.of(film));

        mockMvc.perform(get("/films/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("films/one"))
                .andExpect(model().attribute("film", film));

        verify(filmService).findById(1);
    }

    /**
 * Тестирует сценарий: успешное выполнение при get by id not exists_then show404 with error
 * 
 * @see #whenGetByIdNotExists_thenShow404WithError()
 */


    @Test


    void whenGetByIdNotExists_thenShow404WithError() throws Exception {
        when(filmService.findById(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/films/999"))
                .andExpect(status().isOk())
                .andExpect(view().name("errors/404"))
                .andExpect(model().attribute("message", "Фильм с указанным идентификатором не найден"));

        verify(filmService).findById(999);
    }

    /**
 * Тестирует сценарий: get by negative id_then show404
 * 
 * @see #whenGetByNegativeId_thenShow404()
 */


    @Test


    void whenGetByNegativeId_thenShow404() throws Exception {
        mockMvc.perform(get("/films/-1"))
                .andExpect(status().isOk())
                .andExpect(view().name("errors/404"));
    }

    /**
 * Тестирует сценарий: get all_then service find all called once
 * 
 * @see #whenGetAll_thenServiceFindAllCalledOnce()
 */


    @Test


    void whenGetAll_thenServiceFindAllCalledOnce() throws Exception {
        mockMvc.perform(get("/films"));

        verify(filmService, times(1)).findAll();
        verifyNoMoreInteractions(filmService);
    }
}