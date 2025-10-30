package ru.job4j.cinema.service;

import ru.job4j.cinema.model.Ticket;

import java.util.Collection;

public interface TicketService {

    boolean reserveTicket(int sessionId, int rowNumber, int placeNumber, int userId);

    Collection<Ticket> findByUserId(int userId);
}