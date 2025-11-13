package ru.job4j.cinema.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.FilmSession;
import ru.job4j.cinema.repository.film.FilmRepository;
import ru.job4j.cinema.repository.session.FilmSessionRepository;
import ru.job4j.cinema.service.session.SimpleFilmSessionService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SimpleFilmSessionServiceTest {

    @Mock
    private FilmSessionRepository filmSessionRepository;

    @Mock
    private FilmRepository filmRepository;

    private SimpleFilmSessionService filmSessionService;

    private Film testFilm;
    private FilmSession testFilmSession;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @BeforeEach
    void setUp() {
        filmSessionService = new SimpleFilmSessionService(filmSessionRepository, filmRepository);

        startTime = LocalDateTime.of(2024, 1, 1, 18, 0);
        endTime = LocalDateTime.of(2024, 1, 1, 20, 0);

        testFilm = new Film(1, "Test Film", "Test Description", 2024, 1, 18, 120, 1);
        testFilmSession = new FilmSession(1, 1, 1, startTime, endTime, 500);
    }

    @Test
    /**
 * Тестирует сценарий: корректность возвращаемых данных при find by id_ when session and film exist_ should return session with film
 * 
 * @see #findById_WhenSessionAndFilmExist_ShouldReturnSessionWithFilm()
 */
    void findById_WhenSessionAndFilmExist_ShouldReturnSessionWithFilm() {
        when(filmSessionRepository.findById(1)).thenReturn(Optional.of(testFilmSession));
        when(filmRepository.findById(1)).thenReturn(Optional.of(testFilm));

        Optional<FilmSession> result = filmSessionService.findById(1);

        assertTrue(result.isPresent());
        assertEquals(testFilmSession.getId(), result.get().getId());
        assertEquals(testFilm, result.get().getFilm());
        verify(filmSessionRepository, times(1)).findById(1);
        verify(filmRepository, times(1)).findById(1);
    }

    @Test
    /**
 * Тестирует сценарий: успешное выполнение при find by id_ when session exists but film not exists_ should return session without film
 * 
 * @see #findById_WhenSessionExistsButFilmNotExists_ShouldReturnSessionWithoutFilm()
 */
    void findById_WhenSessionExistsButFilmNotExists_ShouldReturnSessionWithoutFilm() {
        when(filmSessionRepository.findById(1)).thenReturn(Optional.of(testFilmSession));
        when(filmRepository.findById(1)).thenReturn(Optional.empty());

        Optional<FilmSession> result = filmSessionService.findById(1);

        assertTrue(result.isPresent());
        assertEquals(testFilmSession.getId(), result.get().getId());
        assertNull(result.get().getFilm());
        verify(filmSessionRepository, times(1)).findById(1);
        verify(filmRepository, times(1)).findById(1);
    }

    @Test
    /**
 * Тестирует сценарий: успешное выполнение при find by id_ when session not exists_ should return empty
 * 
 * @see #findById_WhenSessionNotExists_ShouldReturnEmpty()
 */
    void findById_WhenSessionNotExists_ShouldReturnEmpty() {
        when(filmSessionRepository.findById(1)).thenReturn(Optional.empty());

        Optional<FilmSession> result = filmSessionService.findById(1);

        assertTrue(result.isEmpty());
        verify(filmSessionRepository, times(1)).findById(1);
        verify(filmRepository, never()).findById(anyInt());
    }

    @Test
    /**
 * Тестирует сценарий: корректность возвращаемых данных при find all_ should return all sessions with films
 * 
 * @see #findAll_ShouldReturnAllSessionsWithFilms()
 */
    void findAll_ShouldReturnAllSessionsWithFilms() {
        FilmSession session1 = new FilmSession(1, 1, 1, startTime, endTime, 500);
        FilmSession session2 = new FilmSession(2, 1, 2, startTime.plusHours(3), endTime.plusHours(3), 600);
        List<FilmSession> sessions = List.of(session1, session2);

        when(filmSessionRepository.findAll()).thenReturn(sessions);
        when(filmRepository.findById(1)).thenReturn(Optional.of(testFilm));

        Collection<FilmSession> result = filmSessionService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(session -> session.getFilm() != null);
        assertThat(result).extracting(FilmSession::getId).containsExactly(1, 2);
        verify(filmSessionRepository, times(1)).findAll();
        verify(filmRepository, times(2)).findById(1);
    }

    @Test
    /**
 * Тестирует сценарий: корректность возвращаемых данных при find all_ when some films not exist_ should return sessions with available films
 * 
 * @see #findAll_WhenSomeFilmsNotExist_ShouldReturnSessionsWithAvailableFilms()
 */
    void findAll_WhenSomeFilmsNotExist_ShouldReturnSessionsWithAvailableFilms() {
        FilmSession session1 = new FilmSession(1, 1, 1, startTime, endTime, 500);
        FilmSession session2 = new FilmSession(2, 2, 2, startTime.plusHours(3), endTime.plusHours(3), 600);
        List<FilmSession> sessions = List.of(session1, session2);

        when(filmSessionRepository.findAll()).thenReturn(sessions);
        when(filmRepository.findById(1)).thenReturn(Optional.of(testFilm));
        when(filmRepository.findById(2)).thenReturn(Optional.empty());

        Collection<FilmSession> result = filmSessionService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.stream().filter(s -> s.getId() == 1).findFirst().get().getFilm()).isNotNull();
        assertThat(result.stream().filter(s -> s.getId() == 2).findFirst().get().getFilm()).isNull();
        verify(filmSessionRepository, times(1)).findAll();
        verify(filmRepository, times(1)).findById(1);
        verify(filmRepository, times(1)).findById(2);
    }

    @Test
    /**
 * Тестирует сценарий: поведение с пустыми данными при find all_ when no sessions_ should return empty collection
 * 
 * @see #findAll_WhenNoSessions_ShouldReturnEmptyCollection()
 */
    void findAll_WhenNoSessions_ShouldReturnEmptyCollection() {
        when(filmSessionRepository.findAll()).thenReturn(List.of());

        Collection<FilmSession> result = filmSessionService.findAll();

        assertThat(result).isEmpty();
        verify(filmSessionRepository, times(1)).findAll();
        verify(filmRepository, never()).findById(anyInt());
    }

    @Test
    /**
 * Тестирует сценарий: обработку ошибки при find by id_ with invalid ids_ should return empty
 * 
 * @see #findById_WithInvalidIds_ShouldReturnEmpty()
 */
    void findById_WithInvalidIds_ShouldReturnEmpty() {
        assertThat(filmSessionService.findById(0)).isEmpty();
        assertThat(filmSessionService.findById(-1)).isEmpty();

        verify(filmSessionRepository, times(1)).findById(0);
        verify(filmSessionRepository, times(1)).findById(-1);
        verify(filmRepository, never()).findById(anyInt());
    }

    @Test
    /**
 * Тестирует сценарий: find by id_ when session found_ should preserve time data
 * 
 * @see #findById_WhenSessionFound_ShouldPreserveTimeData()
 */
    void findById_WhenSessionFound_ShouldPreserveTimeData() {
        when(filmSessionRepository.findById(1)).thenReturn(Optional.of(testFilmSession));
        when(filmRepository.findById(1)).thenReturn(Optional.of(testFilm));

        Optional<FilmSession> result = filmSessionService.findById(1);

        assertThat(result).isPresent();
        assertThat(result.get().getStartTime()).isEqualTo(startTime);
        assertThat(result.get().getEndTime()).isEqualTo(endTime);
        assertThat(result.get().getPrice()).isEqualTo(500);
    }
}