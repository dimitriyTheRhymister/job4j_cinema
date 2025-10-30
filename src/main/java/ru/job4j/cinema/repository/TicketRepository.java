package ru.job4j.cinema.repository;

import ru.job4j.cinema.model.Ticket;

import java.util.Collection;
import java.util.Optional;

public interface TicketRepository {

    Optional<Ticket> findBySessionIdAndRowAndPlace(int sessionId, int rowNumber, int placeNumber);

    boolean reserveTicket(int sessionId, int rowNumber, int placeNumber, int userId);

    Collection<Ticket> findByUserId(int userId);
}