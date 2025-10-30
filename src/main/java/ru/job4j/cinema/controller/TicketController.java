package ru.job4j.cinema.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.cinema.service.FilmService;
import ru.job4j.cinema.service.FilmSessionService;
import ru.job4j.cinema.service.HallService;
import ru.job4j.cinema.service.TicketService;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final FilmSessionService filmSessionService;
    private final FilmService filmService;
    private final HallService hallService;

    public TicketController(TicketService ticketService, FilmSessionService filmSessionService, FilmService filmService, HallService hallService) {
        this.ticketService = ticketService;
        this.filmSessionService = filmSessionService;
        this.filmService = filmService;
        this.hallService = hallService;
    }

    @GetMapping("/buy")
    public String showBuyPage(@RequestParam int sessionId, Model model) {
        var session = filmSessionService.findById(sessionId).orElseThrow(() -> new RuntimeException("Session not found"));

        var film = filmService.findById(session.getFilmId()).orElseThrow(() -> new RuntimeException("Film not found"));
        var hall = hallService.findById(session.getHallId()).orElseThrow(() -> new RuntimeException("Hall not found"));

        model.addAttribute("filmSession", session);
        model.addAttribute("film", film);
        model.addAttribute("hall", hall);

        return "tickets/buy";
    }

    @PostMapping("/buy")
    public String buyTicket(@RequestParam int sessionId, @RequestParam int rowNumber, @RequestParam int placeNumber, Model model, jakarta.servlet.http.HttpServletRequest request) {
        var session = request.getSession();
        var user = (ru.job4j.cinema.model.User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/users/login";
        }

        try {
            var isReserved = ticketService.reserveTicket(sessionId, rowNumber, placeNumber, user.getId());
            if (isReserved) {
                var filmSession = filmSessionService.findById(sessionId).orElseThrow(() -> new RuntimeException("Session not found"));
                var film = filmService.findById(filmSession.getFilmId()).orElseThrow(() -> new RuntimeException("Film not found"));

                model.addAttribute("placeNumber", placeNumber);
                model.addAttribute("rowNumber", rowNumber);
                model.addAttribute("filmSession", filmSession);
                model.addAttribute("film", film);

                return "tickets/success";
            } else {
                model.addAttribute("message", "Не удалось приобрести билет на заданное место. Вероятно оно уже занято.");
                return "tickets/error";
            }
        } catch (Exception exception) {
            model.addAttribute("message", exception.getMessage());
            return "errors/404";
        }
    }

    @GetMapping("/mine")
    public String getUserTickets(Model model, HttpServletRequest request) {
        var session = request.getSession();
        var user = (ru.job4j.cinema.model.User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/users/login";
        }

        var tickets = ticketService.findByUserId(user.getId());
        var ticketsWithDetails = new ArrayList<Map<String, Object>>();

        for (var ticket : tickets) {
            var sessionOptional = filmSessionService.findById(ticket.getSessionId());
            if (sessionOptional.isPresent()) {
                var filmSession = sessionOptional.get();
                var filmOptional = filmService.findById(filmSession.getFilmId());
                if (filmOptional.isPresent()) {
                    var film = filmOptional.get();
                    var map = new HashMap<String, Object>();
                    map.put("ticket", ticket);
                    map.put("film", film);
                    map.put("session", filmSession);
                    ticketsWithDetails.add(map);
                }
            }
        }

        model.addAttribute("tickets", ticketsWithDetails);
        return "tickets/mine";
    }
}