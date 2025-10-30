package ru.job4j.cinema.controller;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalControllerAdviceTest {

    private final GlobalControllerAdvice advice = new GlobalControllerAdvice();

    @Test
    /**
 * Тестирует сценарий: корректность возвращаемых данных при root path_then return home
 * 
 * @see #whenRootPath_thenReturnHome()
 */
    void whenRootPath_thenReturnHome() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/");

        String result = advice.getCurrentPage(request);
        assertEquals("home", result);
    }

    @Test
    /**
 * Тестирует сценарий: корректность возвращаемых данных при index path_then return home
 * 
 * @see #whenIndexPath_thenReturnHome()
 */
    void whenIndexPath_thenReturnHome() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/index");

        String result = advice.getCurrentPage(request);
        assertEquals("home", result);
    }

    @Test
    /**
 * Тестирует сценарий: корректность возвращаемых данных при films path_then return films
 * 
 * @see #whenFilmsPath_thenReturnFilms()
 */
    void whenFilmsPath_thenReturnFilms() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/films");

        String result = advice.getCurrentPage(request);
        assertEquals("films", result);
    }

    @Test
    /**
 * Тестирует сценарий: корректность возвращаемых данных при films with id path_then return films
 * 
 * @see #whenFilmsWithIdPath_thenReturnFilms()
 */
    void whenFilmsWithIdPath_thenReturnFilms() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/films/123");

        String result = advice.getCurrentPage(request);
        assertEquals("films", result);
    }

    @Test
    /**
 * Тестирует сценарий: корректность возвращаемых данных при films create path_then return films
 * 
 * @see #whenFilmsCreatePath_thenReturnFilms()
 */
    void whenFilmsCreatePath_thenReturnFilms() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/films/create");

        String result = advice.getCurrentPage(request);
        assertEquals("films", result);
    }

    @Test
    /**
 * Тестирует сценарий: корректность возвращаемых данных при sessions path_then return sessions
 * 
 * @see #whenSessionsPath_thenReturnSessions()
 */
    void whenSessionsPath_thenReturnSessions() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/sessions");

        String result = advice.getCurrentPage(request);
        assertEquals("sessions", result);
    }

    @Test
    /**
 * Тестирует сценарий: корректность возвращаемых данных при sessions with id path_then return sessions
 * 
 * @see #whenSessionsWithIdPath_thenReturnSessions()
 */
    void whenSessionsWithIdPath_thenReturnSessions() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/sessions/456");

        String result = advice.getCurrentPage(request);
        assertEquals("sessions", result);
    }

    @Test
    /**
 * Тестирует сценарий: корректность возвращаемых данных при halls path_then return halls
 * 
 * @see #whenHallsPath_thenReturnHalls()
 */
    void whenHallsPath_thenReturnHalls() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/halls");

        String result = advice.getCurrentPage(request);
        assertEquals("halls", result);
    }

    @Test
    /**
 * Тестирует сценарий: корректность возвращаемых данных при tickets path_then return tickets
 * 
 * @see #whenTicketsPath_thenReturnTickets()
 */
    void whenTicketsPath_thenReturnTickets() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/tickets");

        String result = advice.getCurrentPage(request);
        assertEquals("tickets", result);
    }

    @Test
    /**
 * Тестирует сценарий: корректность возвращаемых данных при users path_then return users
 * 
 * @see #whenUsersPath_thenReturnUsers()
 */
    void whenUsersPath_thenReturnUsers() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/users");

        String result = advice.getCurrentPage(request);
        assertEquals("users", result);
    }

    @Test
    /**
 * Тестирует сценарий: корректность возвращаемых данных при genres path_then return genres
 * 
 * @see #whenGenresPath_thenReturnGenres()
 */
    void whenGenresPath_thenReturnGenres() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/genres");

        String result = advice.getCurrentPage(request);
        assertEquals("genres", result);
    }

    @Test
    /**
 * Тестирует сценарий: поведение с пустыми данными при unknown path_then return empty
 * 
 * @see #whenUnknownPath_thenReturnEmpty()
 */
    void whenUnknownPath_thenReturnEmpty() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/unknown");

        String result = advice.getCurrentPage(request);
        assertEquals("", result);
    }

    @Test
    /**
 * Тестирует сценарий: поведение с пустыми данными при empty path_then return empty
 * 
 * @see #whenEmptyPath_thenReturnEmpty()
 */
    void whenEmptyPath_thenReturnEmpty() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("");

        String result = advice.getCurrentPage(request);
        assertEquals("", result);
    }

    @Test
    /**
 * Тестирует сценарий: корректность возвращаемых данных при films update path_then return films
 * 
 * @see #whenFilmsUpdatePath_thenReturnFilms()
 */
    void whenFilmsUpdatePath_thenReturnFilms() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/films/update");

        String result = advice.getCurrentPage(request);
        assertEquals("films", result);
    }

    @Test
    /**
 * Тестирует сценарий: поведение с пустыми данными при null path_then return empty
 * 
 * @see #whenNullPath_thenReturnEmpty()
 */
    void whenNullPath_thenReturnEmpty() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        String result = advice.getCurrentPage(request);
        assertEquals("", result);
    }

    @Test
    /**
 * Тестирует сценарий: корректность возвращаемых данных при films with complex path_then return films
 * 
 * @see #whenFilmsWithComplexPath_thenReturnFilms()
 */
    void whenFilmsWithComplexPath_thenReturnFilms() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/films/123/edit/something");

        String result = advice.getCurrentPage(request);
        assertEquals("films", result);
    }
}