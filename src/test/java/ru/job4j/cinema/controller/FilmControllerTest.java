package ru.job4j.cinema.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.Genre;
import ru.job4j.cinema.service.FilmService;

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

    @Test
    /**
 * Тестирует сценарий: корректность возвращаемых данных при get all_then return films list view
 * 
 * @see #whenGetAll_thenReturnFilmsListView()
 */
    void whenGetAll_thenReturnFilmsListView() throws Exception {
        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(view().name("films/list"))
                .andExpect(model().attributeExists("films"));

        verify(filmService).findAll();
    }

    @Test
    /**
 * Тестирует сценарий: успешное выполнение при get by id exists_then return one film view
 * 
 * @see #whenGetByIdExists_thenReturnOneFilmView()
 */
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

    @Test
    /**
 * Тестирует сценарий: успешное выполнение при get by id not exists_then show404 with error
 * 
 * @see #whenGetByIdNotExists_thenShow404WithError()
 */
    void whenGetByIdNotExists_thenShow404WithError() throws Exception {
        when(filmService.findById(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/films/999"))
                .andExpect(status().isOk())
                .andExpect(view().name("errors/404"))
                .andExpect(model().attribute("message", "Фильм с указанным идентификатором не найден"));

        verify(filmService).findById(999);
    }

    @Test
    /**
 * Тестирует сценарий: get by negative id_then show404
 * 
 * @see #whenGetByNegativeId_thenShow404()
 */
    void whenGetByNegativeId_thenShow404() throws Exception {
        mockMvc.perform(get("/films/-1"))
                .andExpect(status().isOk())
                .andExpect(view().name("errors/404"));
    }

    @Test
    /**
 * Тестирует сценарий: get all_then service find all called once
 * 
 * @see #whenGetAll_thenServiceFindAllCalledOnce()
 */
    void whenGetAll_thenServiceFindAllCalledOnce() throws Exception {
        mockMvc.perform(get("/films"));

        verify(filmService, times(1)).findAll();
        verifyNoMoreInteractions(filmService);
    }
}