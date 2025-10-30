package ru.job4j.cinema.service;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.dto.FileDto;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.repository.FilmRepository;
import ru.job4j.cinema.repository.GenreRepository;

import java.util.Collection;
import java.util.Optional;

@ThreadSafe
@Service
public class SimpleFilmService implements FilmService {

    private final FilmRepository filmRepository;
    private final FileService fileService;
    private final GenreRepository genreRepository;

    public SimpleFilmService(FilmRepository filmRepository, FileService fileService, GenreRepository genreRepository) {
        this.filmRepository = filmRepository;
        this.fileService = fileService;
        this.genreRepository = genreRepository;
    }

    private void saveNewFile(Film film, FileDto image) {
        var file = fileService.save(image);
        film.setFileId(file.getId());
    }

    @Override
    public Optional<Film> findById(int id) {
        var filmOptional = filmRepository.findById(id);
        if (filmOptional.isPresent()) {
            var film = filmOptional.get();
            var genreOptional = genreRepository.findById(film.getGenreId());
            genreOptional.ifPresent(film::setGenre);
            return Optional.of(film);
        }
        return Optional.empty();
    }

    @Override
    public Collection<Film> findAll() {
        var films = filmRepository.findAll();
        for (var film : films) {
            var genreOptional = genreRepository.findById(film.getGenreId());
            genreOptional.ifPresent(film::setGenre);
        }
        return films;
    }
}