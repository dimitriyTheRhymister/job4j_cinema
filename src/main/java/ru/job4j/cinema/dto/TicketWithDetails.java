package ru.job4j.cinema.dto;

import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.FilmSession;
import ru.job4j.cinema.model.Ticket;

public class TicketWithDetails {
    private Ticket ticket;
    private FilmSession filmSession;
    private Film film;

    public TicketWithDetails(Ticket ticket, FilmSession filmSession, Film film) {
        this.ticket = ticket;
        this.filmSession = filmSession;
        this.film = film;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public FilmSession getFilmSession() {
        return filmSession;
    }

    public void setFilmSession(FilmSession filmSession) {
        this.filmSession = filmSession;
    }

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }
}
