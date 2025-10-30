package ru.job4j.cinema.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.FilmSession;
import ru.job4j.cinema.service.FilmSessionService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FilmSessionController.class)
class FilmSessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FilmSessionService filmSessionService;

    private final LocalDateTime testStartTime = LocalDateTime.of(2023, 12, 1, 18, 0);
    private final LocalDateTime testEndTime = LocalDateTime.of(2023, 12, 1, 20, 0);

    @Test
    /**
 * Тестирует сценарий: корректность возвращаемых данных при get all_then return sessions list view
 * 
 * @see #whenGetAll_thenReturnSessionsListView()
 */
    void whenGetAll_thenReturnSessionsListView() throws Exception {
        Film film = new Film(1, "Inception", "Description", 2010, 1, 16, 148, 1);
        FilmSession session = new FilmSession(1, 1, 1, testStartTime, testEndTime, 500, film);

        when(filmSessionService.findAll()).thenReturn(List.of(session));

        mockMvc.perform(get("/sessions"))
                .andExpect(status().isOk())
                .andExpect(view().name("sessions/list"))
                .andExpect(model().attributeExists("sessions"))
                .andExpect(model().attribute("sessions", List.of(session)));

        verify(filmSessionService).findAll();
    }
}