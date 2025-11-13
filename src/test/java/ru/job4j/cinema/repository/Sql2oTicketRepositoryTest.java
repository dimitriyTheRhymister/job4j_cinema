package ru.job4j.cinema.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.repository.ticket.Sql2oTicketRepository;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class Sql2oTicketRepositoryTest {

    private Sql2oTicketRepository ticketRepository;
    private Sql2o sql2o;

    @BeforeEach
    void setUp() {
        String url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=PostgreSQL";
        sql2o = new Sql2o(url, "sa", "");
        ticketRepository = new Sql2oTicketRepository(sql2o);

        createTables();
        insertTestData();
    }

    @AfterEach
    void tearDown() {
        clearTables();
    }

    private void createTables() {
        try (var connection = sql2o.open()) {
            connection.createQuery("DROP TABLE IF EXISTS tickets CASCADE").executeUpdate();

            connection.createQuery("""
                CREATE TABLE tickets (
                    id SERIAL PRIMARY KEY,
                    session_id INT NOT NULL,
                    row_number INT NOT NULL,
                    place_number INT NOT NULL,
                    user_id INT NOT NULL,
                    UNIQUE(session_id, row_number, place_number)
                )
            """).executeUpdate();

            connection.createQuery("ALTER TABLE tickets ALTER COLUMN id RESTART WITH 1").executeUpdate();
        }
    }

    private void insertTestData() {
        try (var connection = sql2o.open()) {
            connection.createQuery("""
                INSERT INTO tickets (session_id, row_number, place_number, user_id) VALUES
                (1, 1, 1, 100),
                (1, 1, 2, 100),
                (1, 2, 1, 200),
                (2, 1, 1, 300),
                (2, 1, 2, 400),
                (3, 5, 10, 500)
            """).executeUpdate();
        }
    }

    private void clearTables() {
        try (var connection = sql2o.open()) {
            connection.createQuery("DELETE FROM tickets").executeUpdate();
        }
    }

    @Test
    /**
 * Тестирует сценарий: успешное выполнение при find by session id and row and place exists_then return ticket
 * 
 * @see #whenFindBySessionIdAndRowAndPlaceExists_thenReturnTicket()
 */
    void whenFindBySessionIdAndRowAndPlaceExists_thenReturnTicket() {
        Optional<Ticket> found = ticketRepository.findBySessionIdAndRowAndPlace(1, 1, 1);

        assertThat(found).isPresent();
        Ticket ticket = found.get();
        assertThat(ticket.getId()).isEqualTo(1);
        assertThat(ticket.getSessionId()).isEqualTo(1);
        assertThat(ticket.getRowNumber()).isEqualTo(1);
        assertThat(ticket.getPlaceNumber()).isEqualTo(1);
        assertThat(ticket.getUserId()).isEqualTo(100);
    }

    @Test
    /**
 * Тестирует сценарий: успешное выполнение при find by session id and row and place not exists_then return empty
 * 
 * @see #whenFindBySessionIdAndRowAndPlaceNotExists_thenReturnEmpty()
 */
    void whenFindBySessionIdAndRowAndPlaceNotExists_thenReturnEmpty() {
        Optional<Ticket> found = ticketRepository.findBySessionIdAndRowAndPlace(1, 10, 10);
        assertThat(found).isEmpty();
    }

    @Test
    /**
 * Тестирует сценарий: обработку ошибки при find by invalid parameters_then return empty
 * 
 * @see #whenFindByInvalidParameters_thenReturnEmpty()
 */
    void whenFindByInvalidParameters_thenReturnEmpty() {
        assertThat(ticketRepository.findBySessionIdAndRowAndPlace(0, 1, 1)).isEmpty();
        assertThat(ticketRepository.findBySessionIdAndRowAndPlace(1, 0, 1)).isEmpty();
        assertThat(ticketRepository.findBySessionIdAndRowAndPlace(1, 1, 0)).isEmpty();
        assertThat(ticketRepository.findBySessionIdAndRowAndPlace(-1, -1, -1)).isEmpty();
    }

    @Test
    /**
 * Тестирует сценарий: reserve ticket for free place_then success
 * 
 * @see #whenReserveTicketForFreePlace_thenSuccess()
 */
    void whenReserveTicketForFreePlace_thenSuccess() {
        boolean isReserved = ticketRepository.reserveTicket(3, 5, 8, 800);

        assertThat(isReserved).isTrue();

        Optional<Ticket> reservedTicket = ticketRepository.findBySessionIdAndRowAndPlace(3, 5, 8);
        assertThat(reservedTicket).isPresent();
        assertThat(reservedTicket.get().getUserId()).isEqualTo(800);
        assertThat(reservedTicket.get().getSessionId()).isEqualTo(3);
        assertThat(reservedTicket.get().getRowNumber()).isEqualTo(5);
        assertThat(reservedTicket.get().getPlaceNumber()).isEqualTo(8);
    }

    @Test
    /**
 * Тестирует сценарий: reserve ticket for occupied place_then fail
 * 
 * @see #whenReserveTicketForOccupiedPlace_thenFail()
 */
    void whenReserveTicketForOccupiedPlace_thenFail() {
        boolean isReserved = ticketRepository.reserveTicket(1, 1, 1, 900);

        assertThat(isReserved).isFalse();

        Optional<Ticket> existingTicket = ticketRepository.findBySessionIdAndRowAndPlace(1, 1, 1);
        assertThat(existingTicket).isPresent();
        assertThat(existingTicket.get().getUserId()).isEqualTo(100);
    }

    @Test
    /**
 * Тестирует сценарий: успешное выполнение при find by user id exists_then return user tickets
 * 
 * @see #whenFindByUserIdExists_thenReturnUserTickets()
 */
    void whenFindByUserIdExists_thenReturnUserTickets() {
        Collection<Ticket> userTickets = ticketRepository.findByUserId(100);

        assertThat(userTickets).hasSize(2);
        assertThat(userTickets).allMatch(ticket -> ticket.getUserId() == 100);

        assertThat(userTickets)
                .extracting(Ticket::getPlaceNumber)
                .containsExactlyInAnyOrder(1, 2);

        assertThat(userTickets)
                .extracting(Ticket::getSessionId)
                .containsOnly(1);
    }

    @Test
    /**
 * Тестирует сценарий: успешное выполнение при find by user id not exists_then return empty collection
 * 
 * @see #whenFindByUserIdNotExists_thenReturnEmptyCollection()
 */
    void whenFindByUserIdNotExists_thenReturnEmptyCollection() {
        Collection<Ticket> userTickets = ticketRepository.findByUserId(9999);
        assertThat(userTickets).isEmpty();
    }

    @Test
    /**
 * Тестирует сценарий: обработку ошибки при find by invalid user id_then return empty collection
 * 
 * @see #whenFindByInvalidUserId_thenReturnEmptyCollection()
 */
    void whenFindByInvalidUserId_thenReturnEmptyCollection() {
        assertThat(ticketRepository.findByUserId(0)).isEmpty();
        assertThat(ticketRepository.findByUserId(-1)).isEmpty();
    }

    @Test
    /**
 * Тестирует сценарий: reserve ticket with same session row place different users_then first wins
 * 
 * @see #whenReserveTicketWithSameSessionRowPlaceDifferentUsers_thenFirstWins()
 */
    void whenReserveTicketWithSameSessionRowPlaceDifferentUsers_thenFirstWins() {
        boolean firstReservation = ticketRepository.reserveTicket(50, 1, 1, 2000);
        assertThat(firstReservation).isTrue();

        boolean secondReservation = ticketRepository.reserveTicket(50, 1, 1, 2001);
        assertThat(secondReservation).isFalse();

        Optional<Ticket> ticket = ticketRepository.findBySessionIdAndRowAndPlace(50, 1, 1);
        assertThat(ticket).isPresent();
        assertThat(ticket.get().getUserId()).isEqualTo(2000);
    }

    @Test
    /**
 * Тестирует сценарий: успешное выполнение при reserve ticket with minimum values_then fail because already exists
 * 
 * @see #whenReserveTicketWithMinimumValues_thenFailBecauseAlreadyExists()
 */
    void whenReserveTicketWithMinimumValues_thenFailBecauseAlreadyExists() {
        boolean isReserved = ticketRepository.reserveTicket(1, 1, 1, 1);
        assertThat(isReserved).isFalse();
    }

    @Test
    /**
 * Тестирует сценарий: reserve ticket with new session_then success
 * 
 * @see #whenReserveTicketWithNewSession_thenSuccess()
 */
    void whenReserveTicketWithNewSession_thenSuccess() {
        boolean isReserved = ticketRepository.reserveTicket(999, 1, 1, 6000);

        assertThat(isReserved).isTrue();

        Optional<Ticket> reserved = ticketRepository.findBySessionIdAndRowAndPlace(999, 1, 1);
        assertThat(reserved).isPresent();
        assertThat(reserved.get().getUserId()).isEqualTo(6000);
        assertThat(reserved.get().getSessionId()).isEqualTo(999);
    }

    @Test
    /**
 * Тестирует сценарий: user reserves multiple tickets_then all reserved
 * 
 * @see #whenUserReservesMultipleTickets_thenAllReserved()
 */
    void whenUserReservesMultipleTickets_thenAllReserved() {
        boolean ticket1 = ticketRepository.reserveTicket(10, 1, 1, 7000);
        boolean ticket2 = ticketRepository.reserveTicket(10, 1, 2, 7000);
        boolean ticket3 = ticketRepository.reserveTicket(10, 1, 3, 7000);

        assertThat(ticket1).isTrue();
        assertThat(ticket2).isTrue();
        assertThat(ticket3).isTrue();

        Collection<Ticket> userTickets = ticketRepository.findByUserId(7000);
        assertThat(userTickets).hasSize(3);
        assertThat(userTickets)
                .extracting(Ticket::getPlaceNumber)
                .containsExactlyInAnyOrder(1, 2, 3);
    }

    @Test
    /**
 * Тестирует сценарий: reserve same place in different sessions_then both success
 * 
 * @see #whenReserveSamePlaceInDifferentSessions_thenBothSuccess()
 */
    void whenReserveSamePlaceInDifferentSessions_thenBothSuccess() {
        boolean session1 = ticketRepository.reserveTicket(100, 5, 5, 8000);
        boolean session2 = ticketRepository.reserveTicket(200, 5, 5, 8000);

        assertThat(session1).isTrue();
        assertThat(session2).isTrue();

        assertThat(ticketRepository.findBySessionIdAndRowAndPlace(100, 5, 5)).isPresent();
        assertThat(ticketRepository.findBySessionIdAndRowAndPlace(200, 5, 5)).isPresent();
    }

    @Test
    /**
 * Тестирует сценарий: поведение с пустыми данными при find by user id in empty database_then return empty
 * 
 * @see #whenFindByUserIdInEmptyDatabase_thenReturnEmpty()
 */
    void whenFindByUserIdInEmptyDatabase_thenReturnEmpty() {
        clearTables();

        Collection<Ticket> tickets = ticketRepository.findByUserId(100);
        assertThat(tickets).isEmpty();

        Optional<Ticket> ticket = ticketRepository.findBySessionIdAndRowAndPlace(1, 1, 1);
        assertThat(ticket).isEmpty();
    }
}