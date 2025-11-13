package ru.job4j.cinema.repository.ticket;

import org.springframework.stereotype.Repository;
import org.sql2o.Sql2o;
import ru.job4j.cinema.dto.TicketWithDetails;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.FilmSession;
import ru.job4j.cinema.model.Genre;
import ru.job4j.cinema.model.Ticket;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Optional;

@Repository
public class Sql2oTicketRepository implements TicketRepository {

    private final Sql2o sql2o;

    public Sql2oTicketRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public Optional<Ticket> findBySessionIdAndRowAndPlace(int sessionId, int rowNumber, int placeNumber) {
        try (var connection = sql2o.open()) {
            var sql = """
                    SELECT * FROM tickets
                    WHERE session_id = :sessionId AND row_number = :rowNumber AND place_number = :placeNumber
                    """;
            var query = connection.createQuery(sql)
                    .addParameter("sessionId", sessionId)
                    .addParameter("rowNumber", rowNumber)
                    .addParameter("placeNumber", placeNumber);
            var ticket = query.setColumnMappings(Ticket.COLUMN_MAPPING).executeAndFetchFirst(Ticket.class);
            return Optional.ofNullable(ticket);
        }
    }

    @Override
    public boolean reserveTicket(int sessionId, int rowNumber, int placeNumber, int userId) {
        if (findBySessionIdAndRowAndPlace(sessionId, rowNumber, placeNumber).isPresent()) {
            return false;
        }
        try (var connection = sql2o.open()) {
            var sql = """
                    INSERT INTO tickets(session_id, row_number, place_number, user_id)
                    VALUES (:sessionId, :rowNumber, :placeNumber, :userId)
                    """;
            var query = connection.createQuery(sql)
                    .addParameter("sessionId", sessionId)
                    .addParameter("rowNumber", rowNumber)
                    .addParameter("placeNumber", placeNumber)
                    .addParameter("userId", userId);
            return query.executeUpdate().getResult() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Collection<Ticket> findByUserId(int userId) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("SELECT * FROM tickets WHERE user_id = :userId");
            query.addParameter("userId", userId);
            return query.setColumnMappings(Ticket.COLUMN_MAPPING).executeAndFetch(Ticket.class);
        }
    }

    @Override
    public Collection<TicketWithDetails> findTicketsWithDetailsByUserId(int userId) {
        try (var connection = sql2o.open()) {
            var sql = """
                SELECT t.*, fs.*, f.*, g.id as genre_id, g.name as genre_name
                FROM tickets t
                JOIN film_sessions fs ON t.session_id = fs.id
                JOIN films f ON fs.film_id = f.id
                LEFT JOIN genres g ON f.genre_id = g.id
                WHERE t.user_id = :userId
                ORDER BY fs.start_time DESC
                """;
            var query = connection.createQuery(sql).addParameter("userId", userId);

            return query.executeAndFetch(this::mapRowToTicketWithDetails);
        }
    }

    private TicketWithDetails mapRowToTicketWithDetails(ResultSet rs) throws SQLException {
        Ticket ticket = mapRowToTicket(rs);
        FilmSession filmSession = mapRowToFilmSession(rs);
        Film film = mapRowToFilm(rs);

        return new TicketWithDetails(ticket, filmSession, film);
    }

    private Ticket mapRowToTicket(ResultSet rs) throws SQLException {
        Ticket ticket = new Ticket();
        ticket.setId(rs.getInt("t.id"));
        ticket.setSessionId(rs.getInt("t.session_id"));
        ticket.setRowNumber(rs.getInt("t.row_number"));
        ticket.setPlaceNumber(rs.getInt("t.place_number"));
        ticket.setUserId(rs.getInt("t.user_id"));
        return ticket;
    }

    private FilmSession mapRowToFilmSession(ResultSet rs) throws SQLException {
        FilmSession filmSession = new FilmSession();
        filmSession.setId(rs.getInt("fs.id"));
        filmSession.setFilmId(rs.getInt("fs.film_id"));
        filmSession.setHallId(rs.getInt("fs.hall_id"));

        Timestamp startTimestamp = rs.getTimestamp("fs.start_time");
        Timestamp endTimestamp = rs.getTimestamp("fs.end_time");
        if (startTimestamp != null) {
            filmSession.setStartTime(startTimestamp.toLocalDateTime());
        }
        if (endTimestamp != null) {
            filmSession.setEndTime(endTimestamp.toLocalDateTime());
        }

        filmSession.setPrice(rs.getInt("fs.price"));
        return filmSession;
    }

    private Film mapRowToFilm(ResultSet rs) throws SQLException {
        Film film = new Film();
        film.setId(rs.getInt("f.id"));
        film.setName(rs.getString("f.name"));
        film.setDescription(rs.getString("f.description"));
        film.setReleaseYear(rs.getInt("f.release_year"));
        film.setGenreId(rs.getInt("f.genre_id"));
        film.setMinimalAge(rs.getInt("f.minimal_age"));
        film.setDurationInMinutes(rs.getInt("f.duration_in_minutes"));
        film.setFileId(rs.getInt("f.file_id"));

        if (rs.getObject("genre_id") != null) {
            var genre = new Genre();
            genre.setId(rs.getInt("genre_id"));
            genre.setName(rs.getString("genre_name"));
            film.setGenre(genre);
        }

        return film;
    }
}