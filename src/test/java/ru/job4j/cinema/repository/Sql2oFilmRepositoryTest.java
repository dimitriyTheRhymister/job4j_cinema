package ru.job4j.cinema.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.repository.film.Sql2oFilmRepository;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class Sql2oFilmRepositoryTest {

    private Sql2oFilmRepository filmRepository;
    private Sql2o sql2o;

    @BeforeEach
    void setUp() {
        String url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=PostgreSQL";
        sql2o = new Sql2o(url, "sa", "");
        filmRepository = new Sql2oFilmRepository(sql2o);

        createTables();
        insertTestData();
    }

    @AfterEach
    void tearDown() {
        clearTables();
    }

    private void createTables() {
        try (var connection = sql2o.open()) {
            connection.createQuery("DROP TABLE IF EXISTS films CASCADE").executeUpdate();

            connection.createQuery("""
                CREATE TABLE films (
                    id SERIAL PRIMARY KEY,
                    name VARCHAR NOT NULL,
                    description TEXT NOT NULL,
                    release_year INT NOT NULL,
                    genre_id INT NOT NULL,
                    minimal_age INT NOT NULL,
                    duration_in_minutes INT NOT NULL,
                    file_id INT NOT NULL
                )
            """).executeUpdate();

            connection.createQuery("ALTER TABLE films ALTER COLUMN id RESTART WITH 1").executeUpdate();
        }
    }

    private void insertTestData() {
        try (var connection = sql2o.open()) {
            connection.createQuery("""
                INSERT INTO films (name, description, release_year, genre_id, minimal_age, duration_in_minutes, file_id)
                VALUES
                ('Inception', 'Dream within a dream', 2010, 1, 16, 148, 1),
                ('The Matrix', 'Virtual reality', 1999, 1, 18, 136, 2),
                ('Comedy Movie', 'Funny story', 2022, 2, 12, 95, 3)
            """).executeUpdate();
        }
    }

    private void clearTables() {
        try (var connection = sql2o.open()) {
            connection.createQuery("DELETE FROM films").executeUpdate();
        }
    }

    /**
 * Тестирует сценарий: успешное выполнение при find by id exists_then return film
 * 
 * @see #whenFindByIdExists_thenReturnFilm()
 */


    @Test


    void whenFindByIdExists_thenReturnFilm() {
        Optional<Film> found = filmRepository.findById(1);

        assertThat(found).isPresent();
        Film film = found.get();
        assertThat(film.getId()).isEqualTo(1);
        assertThat(film.getName()).isEqualTo("Inception");
        assertThat(film.getDescription()).isEqualTo("Dream within a dream");

        assertThat(film.getGenreId()).isEqualTo(1);
        assertThat(film.getMinimalAge()).isEqualTo(16);
        assertThat(film.getDurationInMinutes()).isEqualTo(148);
        assertThat(film.getFileId()).isEqualTo(1);
    }

    /**
 * Тестирует сценарий: успешное выполнение при find by id not exists_then return empty
 * 
 * @see #whenFindByIdNotExists_thenReturnEmpty()
 */


    @Test


    void whenFindByIdNotExists_thenReturnEmpty() {
        Optional<Film> found = filmRepository.findById(999);
        assertThat(found).isEmpty();
    }

    /**
 * Тестирует сценарий: поведение с пустыми данными при find by zero id_then return empty
 * 
 * @see #whenFindByZeroId_thenReturnEmpty()
 */


    @Test


    void whenFindByZeroId_thenReturnEmpty() {
        Optional<Film> found = filmRepository.findById(0);
        assertThat(found).isEmpty();
    }

    /**
 * Тестирует сценарий: поведение с пустыми данными при find by negative id_then return empty
 * 
 * @see #whenFindByNegativeId_thenReturnEmpty()
 */


    @Test


    void whenFindByNegativeId_thenReturnEmpty() {
        Optional<Film> found = filmRepository.findById(-1);
        assertThat(found).isEmpty();
    }

    /**
 * Тестирует сценарий: корректность возвращаемых данных при find all_then return all films
 * 
 * @see #whenFindAll_thenReturnAllFilms()
 */


    @Test


    void whenFindAll_thenReturnAllFilms() {
        Collection<Film> films = filmRepository.findAll();

        assertThat(films).hasSize(3);

        assertThat(films)
                .extracting(Film::getName)
                .containsExactlyInAnyOrder("Inception", "The Matrix", "Comedy Movie");
    }

    /**
 * Тестирует сценарий: поведение с пустыми данными при find all empty database_then return empty collection
 * 
 * @see #whenFindAllEmptyDatabase_thenReturnEmptyCollection()
 */


    @Test


    void whenFindAllEmptyDatabase_thenReturnEmptyCollection() {
        clearTables();

        Collection<Film> films = filmRepository.findAll();

        assertThat(films).isEmpty();
    }

    /**
 * Тестирует сценарий: find by id_then all fields mapped correctly
 * 
 * @see #whenFindById_thenAllFieldsMappedCorrectly()
 */


    @Test


    void whenFindById_thenAllFieldsMappedCorrectly() {
        Optional<Film> found = filmRepository.findById(2);

        assertThat(found).isPresent();
        Film film = found.get();

        assertThat(film.getId()).isEqualTo(2);
        assertThat(film.getName()).isEqualTo("The Matrix");
        assertThat(film.getDescription()).isEqualTo("Virtual reality");
        assertThat(film.getGenreId()).isEqualTo(1);
        assertThat(film.getMinimalAge()).isEqualTo(18);
        assertThat(film.getDurationInMinutes()).isEqualTo(136);
        assertThat(film.getFileId()).isEqualTo(2);
    }

    /**
 * Тестирует сценарий: корректность возвращаемых данных при find last inserted film_then return correct data
 * 
 * @see #whenFindLastInsertedFilm_thenReturnCorrectData()
 */


    @Test


    void whenFindLastInsertedFilm_thenReturnCorrectData() {
        Optional<Film> found = filmRepository.findById(3);

        assertThat(found).isPresent();
        Film film = found.get();
        assertThat(film.getName()).isEqualTo("Comedy Movie");
        assertThat(film.getMinimalAge()).isEqualTo(12);
        assertThat(film.getDurationInMinutes()).isEqualTo(95);
    }
}