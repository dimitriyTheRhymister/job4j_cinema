package ru.job4j.cinema.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import org.sql2o.converters.Converter;
import org.sql2o.converters.ConverterException;
import org.sql2o.quirks.NoQuirks;
import org.sql2o.quirks.Quirks;
import ru.job4j.cinema.model.FilmSession;
import ru.job4j.cinema.repository.session.Sql2oFilmSessionRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class Sql2oFilmSessionRepositoryTest {

    private Sql2oFilmSessionRepository filmSessionRepository;
    private Sql2o sql2o;
    private LocalDateTime testStartTime1;
    private LocalDateTime testEndTime1;
    private LocalDateTime testStartTime2;
    private LocalDateTime testEndTime2;

    @BeforeEach
    void setUp() {
        String url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=PostgreSQL";
        sql2o = new Sql2o(url, "sa", "", createConverters());
        filmSessionRepository = new Sql2oFilmSessionRepository(sql2o);

        var now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        testStartTime1 = now.plusHours(1);
        testEndTime1 = now.plusHours(3);
        testStartTime2 = now.plusHours(4);
        testEndTime2 = now.plusHours(6);

        createTables();
        insertTestData();
    }

    @AfterEach
    void tearDown() {
        clearTables();
    }

    private Quirks createConverters() {
        return new NoQuirks() {
            {
                converters.put(LocalDateTime.class, new Converter<LocalDateTime>() {
                    @Override
                    public LocalDateTime convert(Object value) throws ConverterException {
                        if (value == null) {
                            return null;
                        }
                        if (!(value instanceof Timestamp)) {
                            throw new ConverterException("Invalid value to convert");
                        }
                        return ((Timestamp) value).toLocalDateTime();
                    }

                    @Override
                    public Object toDatabaseParam(LocalDateTime value) {
                        return value == null ? null : Timestamp.valueOf(value);
                    }
                });
            }
        };
    }

    private void createTables() {
        try (var connection = sql2o.open()) {
            connection.createQuery("DROP TABLE IF EXISTS film_sessions CASCADE").executeUpdate();

            connection.createQuery("""
                CREATE TABLE film_sessions (
                    id SERIAL PRIMARY KEY,
                    film_id INT NOT NULL,
                    halls_id INT NOT NULL,
                    start_time TIMESTAMP NOT NULL,
                    end_time TIMESTAMP NOT NULL,
                    price INT NOT NULL
                )
            """).executeUpdate();

            connection.createQuery("ALTER TABLE film_sessions ALTER COLUMN id RESTART WITH 1").executeUpdate();
        }
    }

    private void insertTestData() {
        try (var connection = sql2o.open()) {
            connection.createQuery("""
                INSERT INTO film_sessions (film_id, halls_id, start_time, end_time, price)
                VALUES
                (1, 1, :start1, :end1, 500),
                (2, 1, :start2, :end2, 600),
                (1, 2, :start3, :end3, 450)
                """)
                    .addParameter("start1", testStartTime1)
                    .addParameter("end1", testEndTime1)
                    .addParameter("start2", testStartTime2)
                    .addParameter("end2", testEndTime2)
                    .addParameter("start3", testStartTime1.plusDays(1))
                    .addParameter("end3", testEndTime1.plusDays(1))
                    .executeUpdate();
        }
    }

    private void clearTables() {
        try (var connection = sql2o.open()) {
            connection.createQuery("DELETE FROM film_sessions").executeUpdate();
        }
    }

    @Test
    /**
 * Тестирует сценарий: успешное выполнение при find by id exists_then return film session
 * 
 * @see #whenFindByIdExists_thenReturnFilmSession()
 */
    void whenFindByIdExists_thenReturnFilmSession() {
        Optional<FilmSession> found = filmSessionRepository.findById(1);

        assertThat(found).isPresent();
        FilmSession session = found.get();
        assertThat(session.getId()).isEqualTo(1);
        assertThat(session.getFilmId()).isEqualTo(1);
        assertThat(session.getHallId()).isEqualTo(1);
        assertThat(session.getStartTime()).isEqualTo(testStartTime1);
        assertThat(session.getEndTime()).isEqualTo(testEndTime1);
        assertThat(session.getPrice()).isEqualTo(500);
    }

    @Test
    /**
 * Тестирует сценарий: успешное выполнение при find by id not exists_then return empty
 * 
 * @see #whenFindByIdNotExists_thenReturnEmpty()
 */
    void whenFindByIdNotExists_thenReturnEmpty() {
        Optional<FilmSession> found = filmSessionRepository.findById(999);
        assertThat(found).isEmpty();
    }

    @Test
    /**
 * Тестирует сценарий: поведение с пустыми данными при find by zero id_then return empty
 * 
 * @see #whenFindByZeroId_thenReturnEmpty()
 */
    void whenFindByZeroId_thenReturnEmpty() {
        Optional<FilmSession> found = filmSessionRepository.findById(0);
        assertThat(found).isEmpty();
    }

    @Test
    /**
 * Тестирует сценарий: поведение с пустыми данными при find by negative id_then return empty
 * 
 * @see #whenFindByNegativeId_thenReturnEmpty()
 */
    void whenFindByNegativeId_thenReturnEmpty() {
        Optional<FilmSession> found = filmSessionRepository.findById(-1);
        assertThat(found).isEmpty();
    }

    @Test
    /**
 * Тестирует сценарий: корректность возвращаемых данных при find all_then return all film sessions
 * 
 * @see #whenFindAll_thenReturnAllFilmSessions()
 */
    void whenFindAll_thenReturnAllFilmSessions() {
        Collection<FilmSession> filmSessions = filmSessionRepository.findAll();

        assertThat(filmSessions).hasSize(3);

        assertThat(filmSessions)
                .extracting(FilmSession::getFilmId)
                .containsExactlyInAnyOrder(1, 2, 1);

        assertThat(filmSessions)
                .extracting(FilmSession::getPrice)
                .containsExactlyInAnyOrder(500, 600, 450);
    }

    @Test
    /**
 * Тестирует сценарий: find by id_then all fields mapped correctly
 * 
 * @see #whenFindById_thenAllFieldsMappedCorrectly()
 */
    void whenFindById_thenAllFieldsMappedCorrectly() {
        Optional<FilmSession> found = filmSessionRepository.findById(2);

        assertThat(found).isPresent();
        FilmSession session = found.get();

        assertThat(session.getId()).isEqualTo(2);
        assertThat(session.getFilmId()).isEqualTo(2);
        assertThat(session.getHallId()).isEqualTo(1);
        assertThat(session.getStartTime()).isEqualTo(testStartTime2);
        assertThat(session.getEndTime()).isEqualTo(testEndTime2);
        assertThat(session.getPrice()).isEqualTo(600);
    }

    @Test
    /**
 * Тестирует сценарий: корректность возвращаемых данных при find sessions with same film different halls_then return all
 * 
 * @see #whenFindSessionsWithSameFilmDifferentHalls_thenReturnAll()
 */
    void whenFindSessionsWithSameFilmDifferentHalls_thenReturnAll() {
        Collection<FilmSession> filmSessions = filmSessionRepository.findAll();

        var film1Sessions = filmSessions.stream()
                .filter(session -> session.getFilmId() == 1)
                .toList();

        assertThat(film1Sessions).hasSize(2);
        assertThat(film1Sessions)
                .extracting(FilmSession::getHallId)
                .containsExactlyInAnyOrder(1, 2);
    }

    @Test
    /**
 * Тестирует сценарий: поведение с пустыми данными при find all empty database_then return empty collection
 * 
 * @see #whenFindAllEmptyDatabase_thenReturnEmptyCollection()
 */
    void whenFindAllEmptyDatabase_thenReturnEmptyCollection() {
        clearTables();

        Collection<FilmSession> filmSessions = filmSessionRepository.findAll();

        assertThat(filmSessions).isEmpty();
    }

    @Test
    /**
 * Тестирует сценарий: find film session_then time intervals are correct
 * 
 * @see #whenFindFilmSession_thenTimeIntervalsAreCorrect()
 */
    void whenFindFilmSession_thenTimeIntervalsAreCorrect() {
        Optional<FilmSession> found = filmSessionRepository.findById(1);

        assertThat(found).isPresent();
        FilmSession session = found.get();

        assertThat(session.getEndTime()).isAfter(session.getStartTime());

        long durationMinutes = java.time.Duration.between(
                session.getStartTime(), session.getEndTime()
        ).toMinutes();
        assertThat(durationMinutes).isEqualTo(120L);
    }
}