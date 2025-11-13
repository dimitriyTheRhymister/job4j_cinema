package ru.job4j.cinema.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import ru.job4j.cinema.model.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import ru.job4j.cinema.service.film.FilmService;
import ru.job4j.cinema.service.hall.HallService;
import ru.job4j.cinema.service.session.FilmSessionService;
import ru.job4j.cinema.service.ticket.TicketService;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TicketControllerTest {

    private TicketService ticketService;
    private FilmSessionService filmSessionService;
    private FilmService filmService;
    private HallService hallService;
    private TicketController ticketController;
    private Model model;
    private HttpServletRequest request;
    private HttpSession session;

    @BeforeEach
    void setUp() {
        ticketService = mock(TicketService.class);
        filmSessionService = mock(FilmSessionService.class);
        filmService = mock(FilmService.class);
        hallService = mock(HallService.class);
        ticketController = new TicketController(ticketService, filmSessionService, filmService, hallService);
        model = mock(Model.class);
        request = mock(HttpServletRequest.class);
        session = mock(HttpSession.class);
    }

    @Test
    /**
 * Тестирует сценарий: успешное выполнение при show buy page with valid session then return buy view
 * 
 * @see #whenShowBuyPageWithValidSessionThenReturnBuyView()
 */
    void whenShowBuyPageWithValidSessionThenReturnBuyView() {
        FilmSession filmSession = new FilmSession(1, 1, 1,
                LocalDateTime.now(), LocalDateTime.now().plusHours(2), 500);
        Film film = new Film(1, "Test Film", "Description", 2023, 1, 16, 120, 1);
        Hall hall = new Hall(1, "Main Hall", 10, 15, "Large hall");

        when(filmSessionService.findById(1)).thenReturn(Optional.of(filmSession));
        when(filmService.findById(1)).thenReturn(Optional.of(film));
        when(hallService.findById(1)).thenReturn(Optional.of(hall));

        String result = ticketController.showBuyPage(1, model);

        verify(model).addAttribute("filmSession", filmSession);
        verify(model).addAttribute("film", film);
        verify(model).addAttribute("hall", hall);
        assertThat(result).isEqualTo("tickets/buy");
    }

    @Test
    /**
 * Тестирует сценарий: обработку ошибки при show buy page with invalid session then throw exception
 * 
 * @see #whenShowBuyPageWithInvalidSessionThenThrowException()
 */
    void whenShowBuyPageWithInvalidSessionThenThrowException() {
        when(filmSessionService.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ticketController.showBuyPage(1, model))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Session not found");
    }

    @Test
    /**
 * Тестирует сценарий: обработку ошибки при show buy page with invalid film then throw exception
 * 
 * @see #whenShowBuyPageWithInvalidFilmThenThrowException()
 */
    void whenShowBuyPageWithInvalidFilmThenThrowException() {
        FilmSession filmSession = new FilmSession(1, 1, 1,
                LocalDateTime.now(), LocalDateTime.now().plusHours(2), 500);

        when(filmSessionService.findById(1)).thenReturn(Optional.of(filmSession));
        when(filmService.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ticketController.showBuyPage(1, model))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Film not found");
    }

    @Test
    /**
 * Тестирует сценарий: обработку ошибки при show buy page with invalid hall then throw exception
 * 
 * @see #whenShowBuyPageWithInvalidHallThenThrowException()
 */
    void whenShowBuyPageWithInvalidHallThenThrowException() {
        FilmSession filmSession = new FilmSession(1, 1, 1,
                LocalDateTime.now(), LocalDateTime.now().plusHours(2), 500);
        Film film = new Film(1, "Test Film", "Description", 2023, 1, 16, 120, 1);

        when(filmSessionService.findById(1)).thenReturn(Optional.of(filmSession));
        when(filmService.findById(1)).thenReturn(Optional.of(film));
        when(hallService.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ticketController.showBuyPage(1, model))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Hall not found");
    }

    @Test
    /**
 * Тестирует сценарий: успешное выполнение при buy ticket with valid data and user logged in then return success
 * 
 * @see #whenBuyTicketWithValidDataAndUserLoggedInThenReturnSuccess()
 */
    void whenBuyTicketWithValidDataAndUserLoggedInThenReturnSuccess() {
        User user = new User(1, "John Doe", "john@example.com", "password");
        FilmSession filmSession = new FilmSession(1, 1, 1,
                LocalDateTime.now(), LocalDateTime.now().plusHours(2), 500);
        Film film = new Film(1, "Test Film", "Description", 2023, 1, 16, 120, 1);

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);
        when(ticketService.reserveTicket(1, 2, 3, 1)).thenReturn(true);
        when(filmSessionService.findById(1)).thenReturn(Optional.of(filmSession));
        when(filmService.findById(1)).thenReturn(Optional.of(film));

        String result = ticketController.buyTicket(1, 2, 3, model, request);

        verify(model).addAttribute("placeNumber", 3);
        verify(model).addAttribute("rowNumber", 2);
        verify(model).addAttribute("filmSession", filmSession);
        verify(model).addAttribute("film", film);
        assertThat(result).isEqualTo("tickets/success");
    }

    @Test
    /**
 * Тестирует сценарий: корректность возвращаемых данных при get user tickets with user logged in then return mine view
 * 
 * @see #whenGetUserTicketsWithUserLoggedInThenReturnMineView()
 */
    void whenGetUserTicketsWithUserLoggedInThenReturnMineView() {
        User user = new User(1, "John Doe", "john@example.com", "password");
        Ticket ticket = new Ticket(1, 1, 2, 3, 1);
        FilmSession filmSession = new FilmSession(1, 1, 1,
                LocalDateTime.now(), LocalDateTime.now().plusHours(2), 500);
        Film film = new Film(1, "Test Film", "Description", 2023, 1, 16, 120, 1);

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);
        when(ticketService.findByUserId(1)).thenReturn(List.of(ticket));
        when(filmSessionService.findById(1)).thenReturn(Optional.of(filmSession));
        when(filmService.findById(1)).thenReturn(Optional.of(film));

        String result = ticketController.getUserTickets(model, request);

        verify(model).addAttribute(eq("tickets"), argThat((List<Map<String, Object>> tickets) ->
                tickets.size() == 1 &&
                        tickets.get(0).get("ticket") == ticket &&
                        tickets.get(0).get("film") == film &&
                        tickets.get(0).get("session") == filmSession
        ));
        assertThat(result).isEqualTo("tickets/mine");
    }

    @Test
    /**
 * Тестирует сценарий: поведение с пустыми данными при get user tickets with no tickets then return empty list
 * 
 * @see #whenGetUserTicketsWithNoTicketsThenReturnEmptyList()
 */
    void whenGetUserTicketsWithNoTicketsThenReturnEmptyList() {
        User user = new User(1, "John Doe", "john@example.com", "password");

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);
        when(ticketService.findByUserId(1)).thenReturn(Collections.emptyList());

        String result = ticketController.getUserTickets(model, request);

        verify(model).addAttribute("tickets", Collections.emptyList());
        assertThat(result).isEqualTo("tickets/mine");
    }

    @Test
    /**
 * Тестирует сценарий: get user tickets with partial data then handle gracefully
 * 
 * @see #whenGetUserTicketsWithPartialDataThenHandleGracefully()
 */
    void whenGetUserTicketsWithPartialDataThenHandleGracefully() {
        User user = new User(1, "John Doe", "john@example.com", "password");
        Ticket ticket1 = new Ticket(1, 1, 2, 3, 1);
        Ticket ticket2 = new Ticket(2, 999, 4, 5, 1);

        FilmSession filmSession = new FilmSession(1, 1, 1,
                LocalDateTime.now(), LocalDateTime.now().plusHours(2), 500);
        Film film = new Film(1, "Test Film", "Description", 2023, 1, 16, 120, 1);

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);
        when(ticketService.findByUserId(1)).thenReturn(List.of(ticket1, ticket2));
        when(filmSessionService.findById(1)).thenReturn(Optional.of(filmSession));
        when(filmSessionService.findById(999)).thenReturn(Optional.empty());
        when(filmService.findById(1)).thenReturn(Optional.of(film));

        String result = ticketController.getUserTickets(model, request);

        verify(model).addAttribute(eq("tickets"), argThat((List<Map<String, Object>> tickets) ->
                tickets.size() == 1 &&
                        tickets.get(0).get("ticket") == ticket1 &&
                        tickets.get(0).get("film") == film &&
                        tickets.get(0).get("session") == filmSession
        ));
        assertThat(result).isEqualTo("tickets/mine");
    }

    @Test
    /**
 * Тестирует сценарий: get user tickets with missing film data then handle gracefully
 * 
 * @see #whenGetUserTicketsWithMissingFilmDataThenHandleGracefully()
 */
    void whenGetUserTicketsWithMissingFilmDataThenHandleGracefully() {
        User user = new User(1, "John Doe", "john@example.com", "password");
        Ticket ticket = new Ticket(1, 1, 2, 3, 1);
        FilmSession filmSession = new FilmSession(1, 1, 1,
                LocalDateTime.now(), LocalDateTime.now().plusHours(2), 500);

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);
        when(ticketService.findByUserId(1)).thenReturn(List.of(ticket));
        when(filmSessionService.findById(1)).thenReturn(Optional.of(filmSession));
        when(filmService.findById(1)).thenReturn(Optional.empty());

        String result = ticketController.getUserTickets(model, request);

        verify(model).addAttribute(eq("tickets"), argThat((List<Map<String, Object>> tickets) ->
                tickets.isEmpty()
        ));
        assertThat(result).isEqualTo("tickets/mine");
    }

    @Test
    void whenBuyTicketSucceedsThenReturnSuccessPage() {
        User user = new User(1, "John Doe", "john@example.com", "password");
        FilmSession filmSession = new FilmSession();
        filmSession.setId(1);
        filmSession.setFilmId(1);
        Film film = new Film();
        film.setId(1);
        film.setName("Test Film");

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);
        when(ticketService.reserveTicket(1, 2, 3, 1)).thenReturn(true);
        when(filmSessionService.findById(1)).thenReturn(Optional.of(filmSession));
        when(filmService.findById(1)).thenReturn(Optional.of(film));

        String result = ticketController.buyTicket(1, 2, 3, model, request);

        verify(model).addAttribute("placeNumber", 3);
        verify(model).addAttribute("rowNumber", 2);
        verify(model).addAttribute("filmSession", filmSession);
        verify(model).addAttribute("film", film);
        assertThat(result).isEqualTo("tickets/success");
    }

    @Test
    void whenBuyTicketAndSeatAlreadyTakenThenReturnError() {
        User user = new User(1, "John Doe", "john@example.com", "password");
        FilmSession filmSession = new FilmSession(1, 1, 1,
                LocalDateTime.now(), LocalDateTime.now().plusHours(2), 500);
        Film film = new Film(1, "Test Film", "Description", 2023, 1, 16, 120, 1);

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);
        when(filmSessionService.findById(1)).thenReturn(Optional.of(filmSession));
        when(filmService.findById(1)).thenReturn(Optional.of(film));
        when(ticketService.reserveTicket(1, 2, 3, 1)).thenReturn(false);

        String result = ticketController.buyTicket(1, 2, 3, model, request);

        verify(model).addAttribute("message",
                "Не удалось приобрести билет на заданное место. Вероятно оно уже занято.");
        assertThat(result).isEqualTo("tickets/error");
    }

    @Test
    void whenBuyTicketWithInvalidSeatNumbersThenReturnError() {
        User user = new User(1, "John Doe", "john@example.com", "password");
        FilmSession filmSession = new FilmSession(1, 1, 1,
                LocalDateTime.now(), LocalDateTime.now().plusHours(2), 500);
        Film film = new Film(1, "Test Film", "Description", 2023, 1, 16, 120, 1);

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);
        when(filmSessionService.findById(1)).thenReturn(Optional.of(filmSession));
        when(filmService.findById(1)).thenReturn(Optional.of(film));
        when(ticketService.reserveTicket(1, 0, 0, 1)).thenReturn(false);

        String result = ticketController.buyTicket(1, 0, 0, model, request);

        verify(model).addAttribute("message",
                "Не удалось приобрести билет на заданное место. Вероятно оно уже занято.");
        assertThat(result).isEqualTo("tickets/error");
    }

    @Test
    void whenBuyTicketFailsThenReturnErrorPage() {
        User user = new User(1, "John Doe", "john@example.com", "password");
        FilmSession filmSession = new FilmSession(1, 1, 1,
                LocalDateTime.now(), LocalDateTime.now().plusHours(2), 500);
        Film film = new Film(1, "Test Film", "Description", 2023, 1, 16, 120, 1);

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);
        when(filmSessionService.findById(1)).thenReturn(Optional.of(filmSession));
        when(filmService.findById(1)).thenReturn(Optional.of(film));
        when(ticketService.reserveTicket(1, 2, 3, 1)).thenReturn(false);

        String result = ticketController.buyTicket(1, 2, 3, model, request);

        verify(model).addAttribute("message", "Не удалось приобрести билет на заданное место. Вероятно оно уже занято.");
        assertThat(result).isEqualTo("tickets/error");
    }

    @Test
    void whenBuyTicketWithExceptionThenReturnErrorPage() {
        User user = new User(1, "John Doe", "john@example.com", "password");
        FilmSession filmSession = new FilmSession(1, 1, 1,
                LocalDateTime.now(), LocalDateTime.now().plusHours(2), 500);
        Film film = new Film(1, "Test Film", "Description", 2023, 1, 16, 120, 1);

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);
        when(filmSessionService.findById(1)).thenReturn(Optional.of(filmSession));
        when(filmService.findById(1)).thenReturn(Optional.of(film));
        when(ticketService.reserveTicket(1, 2, 3, 1)).thenReturn(false);

        String result = ticketController.buyTicket(1, 2, 3, model, request);

        verify(model).addAttribute("message", "Не удалось приобрести билет на заданное место. Вероятно оно уже занято.");
        assertThat(result).isEqualTo("tickets/error");
    }

    @Test
    void whenBuyTicketWithNonExistentSessionThenReturnError() {
        User user = new User(1, "John Doe", "john@example.com", "password");

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);
        when(filmSessionService.findById(1)).thenReturn(Optional.empty());

        String result = ticketController.buyTicket(1, 2, 3, model, request);

        verify(model).addAttribute("message", "Сеанс не найден");
        assertThat(result).isEqualTo("tickets/error");
    }

    @Test
    void whenBuyTicketWithNonExistentFilmThenReturnError() {
        User user = new User(1, "John Doe", "john@example.com", "password");
        FilmSession filmSession = new FilmSession(1, 1, 1,
                LocalDateTime.now(), LocalDateTime.now().plusHours(2), 500);

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);
        when(filmSessionService.findById(1)).thenReturn(Optional.of(filmSession));
        when(filmService.findById(1)).thenReturn(Optional.empty());

        String result = ticketController.buyTicket(1, 2, 3, model, request);

        verify(model).addAttribute("message", "Фильм не найден");
        assertThat(result).isEqualTo("tickets/error");
    }

    @Test
    void whenBuyTicketWithValidDataAndUserLoggedInThenReturnSuccess2() {
        User user = new User(1, "John Doe", "john@example.com", "password");
        FilmSession filmSession = new FilmSession(1, 1, 1,
                LocalDateTime.now(), LocalDateTime.now().plusHours(2), 500);
        Film film = new Film(1, "Test Film", "Description", 2023, 1, 16, 120, 1);

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);
        when(ticketService.reserveTicket(1, 2, 3, 1)).thenReturn(true);
        when(filmSessionService.findById(1)).thenReturn(Optional.of(filmSession));
        when(filmService.findById(1)).thenReturn(Optional.of(film));

        String result = ticketController.buyTicket(1, 2, 3, model, request);

        verify(model).addAttribute("placeNumber", 3);
        verify(model).addAttribute("rowNumber", 2);
        verify(model).addAttribute("filmSession", filmSession);
        verify(model).addAttribute("film", film);
        assertThat(result).isEqualTo("tickets/success");
    }

    @Test
    void whenGetUserTicketsWithUserLoggedInThenReturnMineView2() {
        User user = new User(1, "John Doe", "john@example.com", "password");
        Ticket ticket = new Ticket(1, 1, 2, 3, 1);
        FilmSession filmSession = new FilmSession(1, 1, 1,
                LocalDateTime.now(), LocalDateTime.now().plusHours(2), 500);
        Film film = new Film(1, "Test Film", "Description", 2023, 1, 16, 120, 1);

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);
        when(ticketService.findByUserId(1)).thenReturn(List.of(ticket));
        when(filmSessionService.findById(1)).thenReturn(Optional.of(filmSession));
        when(filmService.findById(1)).thenReturn(Optional.of(film));

        String result = ticketController.getUserTickets(model, request);

        verify(model).addAttribute(eq("tickets"), argThat((List<Map<String, Object>> tickets) ->
                tickets.size() == 1 &&
                        tickets.get(0).get("ticket") == ticket &&
                        tickets.get(0).get("film") == film &&
                        tickets.get(0).get("session") == filmSession
        ));
        assertThat(result).isEqualTo("tickets/mine");
    }
}