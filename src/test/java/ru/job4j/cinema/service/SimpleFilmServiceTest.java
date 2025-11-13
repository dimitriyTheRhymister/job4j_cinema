package ru.job4j.cinema.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.Genre;
import ru.job4j.cinema.repository.film.FilmRepository;
import ru.job4j.cinema.repository.genre.GenreRepository;
import ru.job4j.cinema.service.file.FileService;
import ru.job4j.cinema.service.film.SimpleFilmService;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SimpleFilmServiceTest {

    @Mock
    private FilmRepository filmRepository;

    @Mock
    private FileService fileService;

    @Mock
    private GenreRepository genreRepository;

    private SimpleFilmService filmService;

    @BeforeEach
    void setUp() {
        filmService = new SimpleFilmService(filmRepository, fileService, genreRepository);
    }

    @Test
    /**
 * Тестирует сценарий: корректность возвращаемых данных при find by id_ when film and genre exist_ should return film with genre
 * 
 * @see #findById_WhenFilmAndGenreExist_ShouldReturnFilmWithGenre()
 */
    void findById_WhenFilmAndGenreExist_ShouldReturnFilmWithGenre() {
        Film film = new Film(1, "Film", "Desc", 2020, 2, 16, 120, 1);
        Genre genre = new Genre(2, "Drama");

        when(filmRepository.findById(1)).thenReturn(Optional.of(film));
        when(genreRepository.findById(2)).thenReturn(Optional.of(genre));

        Optional<Film> result = filmService.findById(1);

        assertThat(result).isPresent();
        assertThat(result.get().getGenre()).isEqualTo(genre);
        verify(filmRepository, times(1)).findById(1);
        verify(genreRepository, times(1)).findById(2);
    }

    @Test
    /**
 * Тестирует сценарий: успешное выполнение при find by id_ when film exists but genre not exists_ should return film without genre
 * 
 * @see #findById_WhenFilmExistsButGenreNotExists_ShouldReturnFilmWithoutGenre()
 */
    void findById_WhenFilmExistsButGenreNotExists_ShouldReturnFilmWithoutGenre() {
        Film film = new Film(1, "Film", "Desc", 2020, 2, 16, 120, 1);

        when(filmRepository.findById(1)).thenReturn(Optional.of(film));
        when(genreRepository.findById(2)).thenReturn(Optional.empty());

        Optional<Film> result = filmService.findById(1);

        assertThat(result).isPresent();
        assertThat(result.get().getGenre()).isNull();
        verify(filmRepository, times(1)).findById(1);
        verify(genreRepository, times(1)).findById(2);
    }

    @Test
    /**
 * Тестирует сценарий: успешное выполнение при find by id_ when film not exists_ should return empty
 * 
 * @see #findById_WhenFilmNotExists_ShouldReturnEmpty()
 */
    void findById_WhenFilmNotExists_ShouldReturnEmpty() {
        when(filmRepository.findById(999)).thenReturn(Optional.empty());

        Optional<Film> result = filmService.findById(999);

        assertThat(result).isEmpty();
        verify(filmRepository, times(1)).findById(999);
        verify(genreRepository, never()).findById(anyInt());
    }

    @Test
    /**
 * Тестирует сценарий: find all_ should load genres for all films
 * 
 * @see #findAll_ShouldLoadGenresForAllFilms()
 */
    void findAll_ShouldLoadGenresForAllFilms() {
        Film film1 = new Film(1, "Film A", "Desc A", 2020, 1, 12, 100, 1);
        Film film2 = new Film(2, "Film B", "Desc B", 2021, 2, 16, 120, 2);
        Genre genre1 = new Genre(1, "Comedy");
        Genre genre2 = new Genre(2, "Drama");

        when(filmRepository.findAll()).thenReturn(List.of(film1, film2));
        when(genreRepository.findById(1)).thenReturn(Optional.of(genre1));
        when(genreRepository.findById(2)).thenReturn(Optional.of(genre2));

        Collection<Film> films = filmService.findAll();

        assertThat(films).hasSize(2);
        assertThat(films).extracting(Film::getGenre).containsExactly(genre1, genre2);
        verify(filmRepository, times(1)).findAll();
        verify(genreRepository, times(1)).findById(1);
        verify(genreRepository, times(1)).findById(2);
    }

    @Test
    /**
 * Тестирует сценарий: find all_ when some genres not exist_ should set available genres
 * 
 * @see #findAll_WhenSomeGenresNotExist_ShouldSetAvailableGenres()
 */
    void findAll_WhenSomeGenresNotExist_ShouldSetAvailableGenres() {
        Film film1 = new Film(1, "Film A", "Desc A", 2020, 1, 12, 100, 1);
        Film film2 = new Film(2, "Film B", "Desc B", 2021, 2, 16, 120, 2);
        Genre genre1 = new Genre(1, "Comedy");

        when(filmRepository.findAll()).thenReturn(List.of(film1, film2));
        when(genreRepository.findById(1)).thenReturn(Optional.of(genre1));
        when(genreRepository.findById(2)).thenReturn(Optional.empty());

        Collection<Film> films = filmService.findAll();

        assertThat(films).hasSize(2);
        assertThat(films.stream().filter(f -> f.getId() == 1).findFirst().get().getGenre()).isEqualTo(genre1);
        assertThat(films.stream().filter(f -> f.getId() == 2).findFirst().get().getGenre()).isNull();
    }

    @Test
    /**
 * Тестирует сценарий: поведение с пустыми данными при find all_ when no films_ should return empty collection
 * 
 * @see #findAll_WhenNoFilms_ShouldReturnEmptyCollection()
 */
    void findAll_WhenNoFilms_ShouldReturnEmptyCollection() {
        when(filmRepository.findAll()).thenReturn(List.of());

        Collection<Film> films = filmService.findAll();

        assertThat(films).isEmpty();
        verify(filmRepository, times(1)).findAll();
        verify(genreRepository, never()).findById(anyInt());
    }

    @Test
    /**
 * Тестирует сценарий: обработку ошибки при find by id_ with invalid ids_ should return empty
 * 
 * @see #findById_WithInvalidIds_ShouldReturnEmpty()
 */
    void findById_WithInvalidIds_ShouldReturnEmpty() {
        assertThat(filmService.findById(0)).isEmpty();
        assertThat(filmService.findById(-1)).isEmpty();

        verify(filmRepository, times(1)).findById(0);
        verify(filmRepository, times(1)).findById(-1);
        verify(genreRepository, never()).findById(anyInt());
    }

    @Test
    /**
 * Тестирует сценарий: find by id_ should not use file service
 * 
 * @see #findById_ShouldNotUseFileService()
 */
    void findById_ShouldNotUseFileService() {
        Film film = new Film(1, "Film", "Desc", 2020, 1, 16, 120, 1);
        Genre genre = new Genre(1, "Action");

        when(filmRepository.findById(1)).thenReturn(Optional.of(film));
        when(genreRepository.findById(1)).thenReturn(Optional.of(genre));

        filmService.findById(1);

        verify(fileService, never()).save(any());
        verify(fileService, never()).getFileById(anyInt());
    }
}