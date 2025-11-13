package ru.job4j.cinema.service.ticket;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.dto.TicketWithDetails;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.repository.ticket.TicketRepository;

import java.util.Collection;

@ThreadSafe
@Service
public class SimpleTicketService implements TicketService {

    private final TicketRepository ticketRepository;

    public SimpleTicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    public boolean reserveTicket(int sessionId, int rowNumber, int placeNumber, int userId) {
        return ticketRepository.reserveTicket(sessionId, rowNumber, placeNumber, userId);
    }

    @Override
    public Collection<Ticket> findByUserId(int userId) {
        return ticketRepository.findByUserId(userId);
    }

    @Override
    public Collection<TicketWithDetails> findTicketsWithDetailsByUserId(int userId) {
        return ticketRepository.findTicketsWithDetailsByUserId(userId);
    }
}