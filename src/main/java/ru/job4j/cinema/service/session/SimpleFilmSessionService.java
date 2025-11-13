package ru.job4j.cinema.service.session;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.model.FilmSession;
import ru.job4j.cinema.repository.film.FilmRepository;
import ru.job4j.cinema.repository.session.FilmSessionRepository;

import java.util.Collection;
import java.util.Optional;

@ThreadSafe
@Service
public class SimpleFilmSessionService implements FilmSessionService {

    private final FilmSessionRepository filmSessionRepository;
    private final FilmRepository filmRepository;

    public SimpleFilmSessionService(FilmSessionRepository filmSessionRepository, FilmRepository filmRepository) {
        this.filmSessionRepository = filmSessionRepository;
        this.filmRepository = filmRepository;
    }

    @Override
    public Optional<FilmSession> findById(int id) {
        var sessionOptional = filmSessionRepository.findById(id);
        if (sessionOptional.isPresent()) {
            var session = sessionOptional.get();
            var filmOptional = filmRepository.findById(session.getFilmId());
            if (filmOptional.isPresent()) {
                session.setFilm(filmOptional.get());
            }
            return Optional.of(session);
        }
        return Optional.empty();
    }

    @Override
    public Collection<FilmSession> findAll() {
        var sessions = filmSessionRepository.findAll();
        for (var session : sessions) {
            var filmOptional = filmRepository.findById(session.getFilmId());
            if (filmOptional.isPresent()) {
                session.setFilm(filmOptional.get());
            }
        }
        return sessions;
    }
}