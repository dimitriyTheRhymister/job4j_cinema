package ru.job4j.cinema.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.cinema.model.Hall;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class Sql2oHallRepositoryTest {

    private Sql2oHallRepository hallRepository;
    private Sql2o sql2o;

    @BeforeEach
    void setUp() {
        String url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=PostgreSQL";
        sql2o = new Sql2o(url, "sa", "");
        hallRepository = new Sql2oHallRepository(sql2o);

        createTables();
        insertTestData();
    }

    @AfterEach
    void tearDown() {
        clearTables();
    }

    private void createTables() {
        try (var connection = sql2o.open()) {
            connection.createQuery("DROP TABLE IF EXISTS halls CASCADE").executeUpdate();

            connection.createQuery("""
                CREATE TABLE halls (
                    id SERIAL PRIMARY KEY,
                    name VARCHAR NOT NULL,
                    row_count INT NOT NULL,
                    place_count INT NOT NULL,
                    description TEXT
                )
            """).executeUpdate();

            connection.createQuery("ALTER TABLE halls ALTER COLUMN id RESTART WITH 1").executeUpdate();
        }
    }

    private void insertTestData() {
        try (var connection = sql2o.open()) {
            connection.createQuery("""
                INSERT INTO halls (name, row_count, place_count, description) VALUES
                ('Main Hall', 10, 15, 'Main cinema hall with comfortable seats'),
                ('VIP Hall', 5, 8, 'VIP hall with premium seats and service'),
                ('Small Hall', 6, 10, 'Small hall for private screenings')
            """).executeUpdate();
        }
    }

    private void clearTables() {
        try (var connection = sql2o.open()) {
            connection.createQuery("DELETE FROM halls").executeUpdate();
        }
    }

    @Test
    /**
 * Тестирует сценарий: успешное выполнение при find by id exists_then return hall
 * 
 * @see #whenFindByIdExists_thenReturnHall()
 */
    void whenFindByIdExists_thenReturnHall() {
        Optional<Hall> found = hallRepository.findById(1);

        assertThat(found).isPresent();
        Hall hall = found.get();
        assertThat(hall.getId()).isEqualTo(1);
        assertThat(hall.getName()).isEqualTo("Main Hall");
        assertThat(hall.getRowCount()).isEqualTo(10);
        assertThat(hall.getPlaceCount()).isEqualTo(15);
        assertThat(hall.getDescription()).isEqualTo("Main cinema hall with comfortable seats");
    }

    @Test
    /**
 * Тестирует сценарий: успешное выполнение при find by id not exists_then return empty
 * 
 * @see #whenFindByIdNotExists_thenReturnEmpty()
 */
    void whenFindByIdNotExists_thenReturnEmpty() {
        Optional<Hall> found = hallRepository.findById(999);
        assertThat(found).isEmpty();
    }

    @Test
    /**
 * Тестирует сценарий: поведение с пустыми данными при find by id zero_then return empty
 * 
 * @see #whenFindByIdZero_thenReturnEmpty()
 */
    void whenFindByIdZero_thenReturnEmpty() {
        Optional<Hall> found = hallRepository.findById(0);
        assertThat(found).isEmpty();
    }

    @Test
    /**
 * Тестирует сценарий: поведение с пустыми данными при find by id negative_then return empty
 * 
 * @see #whenFindByIdNegative_thenReturnEmpty()
 */
    void whenFindByIdNegative_thenReturnEmpty() {
        Optional<Hall> found = hallRepository.findById(-1);
        assertThat(found).isEmpty();
    }

    @Test
    /**
 * Тестирует сценарий: корректность возвращаемых данных при find different halls_then return correct data
 * 
 * @see #whenFindDifferentHalls_thenReturnCorrectData()
 */
    void whenFindDifferentHalls_thenReturnCorrectData() {
        Optional<Hall> vipHall = hallRepository.findById(2);
        assertThat(vipHall).isPresent();
        assertThat(vipHall.get().getName()).isEqualTo("VIP Hall");
        assertThat(vipHall.get().getRowCount()).isEqualTo(5);
        assertThat(vipHall.get().getPlaceCount()).isEqualTo(8);
        assertThat(vipHall.get().getDescription()).isEqualTo("VIP hall with premium seats and service");

        Optional<Hall> smallHall = hallRepository.findById(3);
        assertThat(smallHall).isPresent();
        assertThat(smallHall.get().getName()).isEqualTo("Small Hall");
        assertThat(smallHall.get().getRowCount()).isEqualTo(6);
        assertThat(smallHall.get().getPlaceCount()).isEqualTo(10);
        assertThat(smallHall.get().getDescription()).isEqualTo("Small hall for private screenings");
    }

    @Test
    /**
 * Тестирует сценарий: find by id_then all fields mapped correctly
 * 
 * @see #whenFindById_thenAllFieldsMappedCorrectly()
 */
    void whenFindById_thenAllFieldsMappedCorrectly() {
        Optional<Hall> found = hallRepository.findById(1);

        assertThat(found).isPresent();
        Hall hall = found.get();

        assertThat(hall.getId()).isEqualTo(1);
        assertThat(hall.getName()).isEqualTo("Main Hall");
        assertThat(hall.getRowCount()).isEqualTo(10);
        assertThat(hall.getPlaceCount()).isEqualTo(15);
        assertThat(hall.getDescription()).isEqualTo("Main cinema hall with comfortable seats");

        int totalSeats = hall.getRowCount() * hall.getPlaceCount();
        assertThat(totalSeats).isEqualTo(150);
    }

    @Test
    /**
 * Тестирует сценарий: поведение с пустыми данными при find by id after clear_then return empty
 * 
 * @see #whenFindByIdAfterClear_thenReturnEmpty()
 */
    void whenFindByIdAfterClear_thenReturnEmpty() {
        clearTables();

        Optional<Hall> found = hallRepository.findById(1);
        assertThat(found).isEmpty();
    }

    @Test
    /**
 * Тестирует сценарий: find halls_then capacity calculated correctly
 * 
 * @see #whenFindHalls_thenCapacityCalculatedCorrectly()
 */
    void whenFindHalls_thenCapacityCalculatedCorrectly() {
        Optional<Hall> mainHall = hallRepository.findById(1);
        Optional<Hall> vipHall = hallRepository.findById(2);
        Optional<Hall> smallHall = hallRepository.findById(3);

        assertThat(mainHall).isPresent();
        assertThat(vipHall).isPresent();
        assertThat(smallHall).isPresent();

        assertThat(mainHall.get().getRowCount() * mainHall.get().getPlaceCount()).isEqualTo(150);
        assertThat(vipHall.get().getRowCount() * vipHall.get().getPlaceCount()).isEqualTo(40);
        assertThat(smallHall.get().getRowCount() * smallHall.get().getPlaceCount()).isEqualTo(60);
    }

    @Test
    /**
 * Тестирует сценарий: find halls_then each has unique characteristics
 * 
 * @see #whenFindHalls_thenEachHasUniqueCharacteristics()
 */
    void whenFindHalls_thenEachHasUniqueCharacteristics() {
        Optional<Hall> mainHall = hallRepository.findById(1);
        Optional<Hall> vipHall = hallRepository.findById(2);

        assertThat(mainHall).isPresent();
        assertThat(vipHall).isPresent();

        assertThat(mainHall.get().getRowCount()).isNotEqualTo(vipHall.get().getRowCount());
        assertThat(mainHall.get().getPlaceCount()).isNotEqualTo(vipHall.get().getPlaceCount());
        assertThat(mainHall.get().getName()).isNotEqualTo(vipHall.get().getName());
    }
}