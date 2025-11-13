package ru.job4j.cinema.repository.ticket;

import ru.job4j.cinema.dto.TicketWithDetails;
import ru.job4j.cinema.model.Ticket;

import java.util.Collection;
import java.util.Optional;

public interface TicketRepository {

    Optional<Ticket> findBySessionIdAndRowAndPlace(int sessionId, int rowNumber, int placeNumber);

    boolean reserveTicket(int sessionId, int rowNumber, int placeNumber, int userId);

    Collection<Ticket> findByUserId(int userId);

    Collection<TicketWithDetails> findTicketsWithDetailsByUserId(int userId);
}