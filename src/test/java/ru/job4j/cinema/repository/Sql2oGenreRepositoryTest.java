package ru.job4j.cinema.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.cinema.model.Genre;
import ru.job4j.cinema.repository.genre.Sql2oGenreRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class Sql2oGenreRepositoryTest {

    private Sql2oGenreRepository genreRepository;
    private Sql2o sql2o;

    @BeforeEach
    void setUp() {
        String url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=PostgreSQL";
        sql2o = new Sql2o(url, "sa", "");
        genreRepository = new Sql2oGenreRepository(sql2o);

        createTables();
        insertTestData();
    }

    @AfterEach
    void tearDown() {
        clearTables();
    }

    private void createTables() {
        try (var connection = sql2o.open()) {
            connection.createQuery("DROP TABLE IF EXISTS genres CASCADE").executeUpdate();

            connection.createQuery("""
                CREATE TABLE genres (
                    id SERIAL PRIMARY KEY,
                    name VARCHAR NOT NULL UNIQUE
                )
            """).executeUpdate();

            connection.createQuery("ALTER TABLE genres ALTER COLUMN id RESTART WITH 1").executeUpdate();
        }
    }

    private void insertTestData() {
        try (var connection = sql2o.open()) {
            connection.createQuery("""
                INSERT INTO genres (name) VALUES
                ('Action'),
                ('Comedy'),
                ('Drama'),
                ('Sci-Fi'),
                ('Horror')
            """).executeUpdate();
        }
    }

    private void clearTables() {
        try (var connection = sql2o.open()) {
            connection.createQuery("DELETE FROM genres").executeUpdate();
        }
    }

    @Test
    /**
 * Тестирует сценарий: успешное выполнение при find by id exists_then return genre
 * 
 * @see #whenFindByIdExists_thenReturnGenre()
 */
    void whenFindByIdExists_thenReturnGenre() {
        Optional<Genre> found = genreRepository.findById(1);

        assertThat(found).isPresent();
        Genre genre = found.get();
        assertThat(genre.getId()).isEqualTo(1);
        assertThat(genre.getName()).isEqualTo("Action");
    }

    @Test
    /**
 * Тестирует сценарий: успешное выполнение при find by id not exists_then return empty
 * 
 * @see #whenFindByIdNotExists_thenReturnEmpty()
 */
    void whenFindByIdNotExists_thenReturnEmpty() {
        Optional<Genre> found = genreRepository.findById(999);
        assertThat(found).isEmpty();
    }

    @Test
    /**
 * Тестирует сценарий: поведение с пустыми данными при find by id zero_then return empty
 * 
 * @see #whenFindByIdZero_thenReturnEmpty()
 */
    void whenFindByIdZero_thenReturnEmpty() {
        Optional<Genre> found = genreRepository.findById(0);
        assertThat(found).isEmpty();
    }

    @Test
    /**
 * Тестирует сценарий: поведение с пустыми данными при find by id negative_then return empty
 * 
 * @see #whenFindByIdNegative_thenReturnEmpty()
 */
    void whenFindByIdNegative_thenReturnEmpty() {
        Optional<Genre> found = genreRepository.findById(-1);
        assertThat(found).isEmpty();
    }

    @Test
    /**
 * Тестирует сценарий: корректность возвращаемых данных при find different genres_then return correct data
 * 
 * @see #whenFindDifferentGenres_thenReturnCorrectData()
 */
    void whenFindDifferentGenres_thenReturnCorrectData() {
        Optional<Genre> comedy = genreRepository.findById(2);
        assertThat(comedy).isPresent();
        assertThat(comedy.get().getName()).isEqualTo("Comedy");

        Optional<Genre> drama = genreRepository.findById(3);
        assertThat(drama).isPresent();
        assertThat(drama.get().getName()).isEqualTo("Drama");

        Optional<Genre> sciFi = genreRepository.findById(4);
        assertThat(sciFi).isPresent();
        assertThat(sciFi.get().getName()).isEqualTo("Sci-Fi");
    }

    @Test
    /**
 * Тестирует сценарий: корректность возвращаемых данных при find last genre_then return horror
 * 
 * @see #whenFindLastGenre_thenReturnHorror()
 */
    void whenFindLastGenre_thenReturnHorror() {
        Optional<Genre> found = genreRepository.findById(5);

        assertThat(found).isPresent();
        Genre genre = found.get();
        assertThat(genre.getId()).isEqualTo(5);
        assertThat(genre.getName()).isEqualTo("Horror");
    }

    @Test
    /**
 * Тестирует сценарий: find by id_then all fields mapped correctly
 * 
 * @see #whenFindById_thenAllFieldsMappedCorrectly()
 */
    void whenFindById_thenAllFieldsMappedCorrectly() {
        Optional<Genre> found = genreRepository.findById(1);

        assertThat(found).isPresent();
        Genre genre = found.get();

        assertThat(genre.getId()).isEqualTo(1);
        assertThat(genre.getName()).isEqualTo("Action");
        assertThat(genre.getName()).isNotBlank();
    }

    @Test
    /**
 * Тестирует сценарий: поведение с пустыми данными при find by id after clear_then return empty
 * 
 * @see #whenFindByIdAfterClear_thenReturnEmpty()
 */
    void whenFindByIdAfterClear_thenReturnEmpty() {
        clearTables();

        Optional<Genre> found = genreRepository.findById(1);
        assertThat(found).isEmpty();
    }
}