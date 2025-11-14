package ru.job4j.cinema.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.cinema.model.File;
import ru.job4j.cinema.repository.file.Sql2oFileRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class Sql2oFileRepositoryTest {

    private Sql2oFileRepository fileRepository;
    private Sql2o sql2o;

    @BeforeEach
    void setUp() {
        String url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=PostgreSQL";
        sql2o = new Sql2o(url, "sa", "");
        fileRepository = new Sql2oFileRepository(sql2o);

        createTables();
        insertTestData();
    }

    @AfterEach
    void tearDown() {
        clearTables();
    }

    private void createTables() {
        try (var connection = sql2o.open()) {
            connection.createQuery("DROP TABLE IF EXISTS files CASCADE").executeUpdate();

            connection.createQuery("""
                CREATE TABLE files (
                    id SERIAL PRIMARY KEY,
                    name VARCHAR NOT NULL,
                    path VARCHAR NOT NULL UNIQUE
                )
            """).executeUpdate();

            connection.createQuery("ALTER TABLE files ALTER COLUMN id RESTART WITH 1").executeUpdate();
        }
    }

    private void insertTestData() {
        try (var connection = sql2o.open()) {
            connection.createQuery("""
                INSERT INTO files (name, path) VALUES
                ('image1.jpg', '/uploads/images/image1.jpg'),
                ('image2.png', '/uploads/images/image2.png')
            """).executeUpdate();
        }
    }

    private void clearTables() {
        try (var connection = sql2o.open()) {
            connection.createQuery("DELETE FROM files").executeUpdate();
        }
    }

    /**
 * Тестирует сценарий: корректность возвращаемых данных при save_then returns file with id
 * 
 * @see #whenSave_thenReturnsFileWithId()
 */


    @Test


    void whenSave_thenReturnsFileWithId() {
        var file = new File(0, "document.pdf", "/uploads/documents/document.pdf");

        var savedFile = fileRepository.save(file);

        assertThat(savedFile.getId()).isGreaterThan(0);
        assertThat(savedFile.getName()).isEqualTo("document.pdf");
        assertThat(savedFile.getPath()).isEqualTo("/uploads/documents/document.pdf");
    }

    /**
 * Тестирует сценарий: успешное выполнение при find by id exists_then return file
 * 
 * @see #whenFindByIdExists_thenReturnFile()
 */


    @Test


    void whenFindByIdExists_thenReturnFile() {
        var file = new File(0, "movie.mp4", "/uploads/videos/movie.mp4");
        var saved = fileRepository.save(file);

        Optional<File> found = fileRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(saved.getId());
        assertThat(found.get().getName()).isEqualTo("movie.mp4");
        assertThat(found.get().getPath()).isEqualTo("/uploads/videos/movie.mp4");
    }

    /**
 * Тестирует сценарий: успешное выполнение при find by id not exists_then return empty
 * 
 * @see #whenFindByIdNotExists_thenReturnEmpty()
 */


    @Test


    void whenFindByIdNotExists_thenReturnEmpty() {
        Optional<File> found = fileRepository.findById(999);
        assertThat(found).isEmpty();
    }

    /**
 * Тестирует сценарий: save multiple files_then all have unique ids
 * 
 * @see #whenSaveMultipleFiles_thenAllHaveUniqueIds()
 */


    @Test


    void whenSaveMultipleFiles_thenAllHaveUniqueIds() {
        var file1 = fileRepository.save(new File(0, "file1.txt", "/path/to/file1.txt"));
        var file2 = fileRepository.save(new File(0, "file2.txt", "/path/to/file2.txt"));
        var file3 = fileRepository.save(new File(0, "file3.txt", "/path/to/file3.txt"));

        assertThat(file1.getId()).isNotEqualTo(file2.getId());
        assertThat(file2.getId()).isNotEqualTo(file3.getId());
        assertThat(file1.getId()).isNotEqualTo(file3.getId());

        assertThat(fileRepository.findById(file1.getId())).isPresent();
        assertThat(fileRepository.findById(file2.getId())).isPresent();
        assertThat(fileRepository.findById(file3.getId())).isPresent();
    }

    /**
 * Тестирует сценарий: корректность возвращаемых данных при find pre inserted file_then returns correct data
 * 
 * @see #whenFindPreInsertedFile_thenReturnsCorrectData()
 */


    @Test


    void whenFindPreInsertedFile_thenReturnsCorrectData() {
        Optional<File> found = fileRepository.findById(1);

        assertThat(found).isPresent();
        File result = found.get();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("image1.jpg");
        assertThat(result.getPath()).isEqualTo("/uploads/images/image1.jpg");
    }

    /**
 * Тестирует сценарий: save file with same name but different path_then different ids
 * 
 * @see #whenSaveFileWithSameNameButDifferentPath_thenDifferentIds()
 */


    @Test


    void whenSaveFileWithSameNameButDifferentPath_thenDifferentIds() {
        var file1 = fileRepository.save(new File(0, "same_name.jpg", "/path1/same_name.jpg"));
        var file2 = fileRepository.save(new File(0, "same_name.jpg", "/path2/same_name.jpg"));

        assertThat(file1.getId()).isNotEqualTo(file2.getId());
        assertThat(fileRepository.findById(file1.getId())).isPresent();
        assertThat(fileRepository.findById(file2.getId())).isPresent();

        assertThat(file1.getPath()).isNotEqualTo(file2.getPath());
        assertThat(file1.getName()).isEqualTo(file2.getName());
    }

    /**
 * Тестирует сценарий: поведение с пустыми данными при find by zero id_then return empty
 * 
 * @see #whenFindByZeroId_thenReturnEmpty()
 */


    @Test


    void whenFindByZeroId_thenReturnEmpty() {
        Optional<File> found = fileRepository.findById(0);
        assertThat(found).isEmpty();
    }

    /**
 * Тестирует сценарий: поведение с пустыми данными при find by negative id_then return empty
 * 
 * @see #whenFindByNegativeId_thenReturnEmpty()
 */


    @Test


    void whenFindByNegativeId_thenReturnEmpty() {
        Optional<File> found = fileRepository.findById(-1);
        assertThat(found).isEmpty();
    }

    /**
 * Тестирует сценарий: save after pre inserted data_then id continues sequence
 * 
 * @see #whenSaveAfterPreInsertedData_thenIdContinuesSequence()
 */


    @Test


    void whenSaveAfterPreInsertedData_thenIdContinuesSequence() {
        var newFile = fileRepository.save(new File(0, "new_file.jpg", "/path/new_file.jpg"));

        assertThat(newFile.getId()).isEqualTo(3);
    }

    /**
 * Тестирует сценарий: find by id_then all fields mapped correctly
 *
 * @see #whenFindById_thenAllFieldsMappedCorrectly()
 */


    @Test


    void whenFindById_thenAllFieldsMappedCorrectly() {
        var originalFile = new File(0, "test.jpg", "/test/path/test.jpg");
        var savedFile = fileRepository.save(originalFile);

        Optional<File> found = fileRepository.findById(savedFile.getId());

        assertThat(found).isPresent();
        File result = found.get();
        assertThat(result).usingRecursiveComparison().isEqualTo(savedFile);
    }
}