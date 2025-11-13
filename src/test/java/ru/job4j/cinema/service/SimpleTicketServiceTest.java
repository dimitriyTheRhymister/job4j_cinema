package ru.job4j.cinema.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.repository.ticket.TicketRepository;
import ru.job4j.cinema.service.ticket.SimpleTicketService;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SimpleTicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private SimpleTicketService ticketService;

    @Test
    /**
 * Тестирует сценарий: корректность возвращаемых данных при reserve ticket_ when reservation successful_ should return true
 * 
 * @see #reserveTicket_WhenReservationSuccessful_ShouldReturnTrue()
 */
    void reserveTicket_WhenReservationSuccessful_ShouldReturnTrue() {
        when(ticketRepository.reserveTicket(1, 5, 10, 100)).thenReturn(true);

        boolean result = ticketService.reserveTicket(1, 5, 10, 100);

        assertTrue(result);
        verify(ticketRepository, times(1)).reserveTicket(1, 5, 10, 100);
    }

    @Test
    /**
 * Тестирует сценарий: корректность возвращаемых данных при reserve ticket_ when reservation failed_ should return false
 * 
 * @see #reserveTicket_WhenReservationFailed_ShouldReturnFalse()
 */
    void reserveTicket_WhenReservationFailed_ShouldReturnFalse() {
        when(ticketRepository.reserveTicket(1, 5, 10, 100)).thenReturn(false);

        boolean result = ticketService.reserveTicket(1, 5, 10, 100);

        assertFalse(result);
        verify(ticketRepository, times(1)).reserveTicket(1, 5, 10, 100);
    }

    @Test
    /**
 * Тестирует сценарий: корректность возвращаемых данных при find by user id_ when tickets exist_ should return tickets
 * 
 * @see #findByUserId_WhenTicketsExist_ShouldReturnTickets()
 */
    void findByUserId_WhenTicketsExist_ShouldReturnTickets() {
        Ticket ticket1 = new Ticket(1, 1, 5, 10, 100);
        Ticket ticket2 = new Ticket(2, 1, 5, 11, 100);
        List<Ticket> expectedTickets = List.of(ticket1, ticket2);

        when(ticketRepository.findByUserId(100)).thenReturn(expectedTickets);

        Collection<Ticket> result = ticketService.findByUserId(100);

        assertThat(result)
                .hasSize(2)
                .containsExactly(ticket1, ticket2);
        verify(ticketRepository, times(1)).findByUserId(100);
    }

    @Test
    /**
 * Тестирует сценарий: поведение с пустыми данными при find by user id_ when no tickets_ should return empty collection
 * 
 * @see #findByUserId_WhenNoTickets_ShouldReturnEmptyCollection()
 */
    void findByUserId_WhenNoTickets_ShouldReturnEmptyCollection() {
        when(ticketRepository.findByUserId(100)).thenReturn(List.of());

        Collection<Ticket> result = ticketService.findByUserId(100);

        assertThat(result).isEmpty();
        verify(ticketRepository, times(1)).findByUserId(100);
    }

    @Test
    /**
 * Тестирует сценарий: reserve ticket_ with different parameters_ should call repository with correct params
 * 
 * @see #reserveTicket_WithDifferentParameters_ShouldCallRepositoryWithCorrectParams()
 */
    void reserveTicket_WithDifferentParameters_ShouldCallRepositoryWithCorrectParams() {
        when(ticketRepository.reserveTicket(anyInt(), anyInt(), anyInt(), anyInt()))
                .thenReturn(true);

        ticketService.reserveTicket(3, 4, 5, 200);

        verify(ticketRepository, times(1)).reserveTicket(3, 4, 5, 200);
    }

    @Test
    /**
 * Тестирует сценарий: find by user id_ with different user ids_ should call repository with correct user id
 * 
 * @see #findByUserId_WithDifferentUserIds_ShouldCallRepositoryWithCorrectUserId()
 */
    void findByUserId_WithDifferentUserIds_ShouldCallRepositoryWithCorrectUserId() {
        when(ticketRepository.findByUserId(anyInt())).thenReturn(List.of());

        ticketService.findByUserId(300);

        verify(ticketRepository, times(1)).findByUserId(300);
    }

    @Test
    /**
 * Тестирует сценарий: reserve ticket_ with minimum values_ should work
 * 
 * @see #reserveTicket_WithMinimumValues_ShouldWork()
 */
    void reserveTicket_WithMinimumValues_ShouldWork() {
        when(ticketRepository.reserveTicket(1, 1, 1, 1)).thenReturn(true);

        boolean result = ticketService.reserveTicket(1, 1, 1, 1);

        assertTrue(result);
        verify(ticketRepository, times(1)).reserveTicket(1, 1, 1, 1);
    }

    @Test
    /**
 * Тестирует сценарий: methods_ should directly delegate to repository
 * 
 * @see #methods_ShouldDirectlyDelegateToRepository()
 */
    void methods_ShouldDirectlyDelegateToRepository() {
        when(ticketRepository.reserveTicket(1, 2, 3, 4)).thenReturn(true);
        when(ticketRepository.findByUserId(5)).thenReturn(List.of(new Ticket(1, 1, 2, 3, 5)));

        boolean reserveResult = ticketService.reserveTicket(1, 2, 3, 4);
        Collection<Ticket> findResult = ticketService.findByUserId(5);

        assertTrue(reserveResult);
        assertThat(findResult).hasSize(1);
    }
}