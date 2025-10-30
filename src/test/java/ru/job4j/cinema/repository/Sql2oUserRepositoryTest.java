package ru.job4j.cinema.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.cinema.model.User;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class Sql2oUserRepositoryTest {

    private Sql2oUserRepository userRepository;
    private Sql2o sql2o;

    @BeforeEach
    void setUp() {
        String url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=PostgreSQL";
        sql2o = new Sql2o(url, "sa", "");
        userRepository = new Sql2oUserRepository(sql2o);

        createTables();
        insertTestData();
    }

    @AfterEach
    void tearDown() {
        clearTables();
    }

    private void createTables() {
        try (var connection = sql2o.open()) {
            connection.createQuery("DROP TABLE IF EXISTS users CASCADE").executeUpdate();

            connection.createQuery("""
                CREATE TABLE users (
                    id SERIAL PRIMARY KEY,
                    full_name VARCHAR NOT NULL,
                    email VARCHAR NOT NULL UNIQUE,
                    password VARCHAR NOT NULL
                )
            """).executeUpdate();

            connection.createQuery("ALTER TABLE users ALTER COLUMN id RESTART WITH 1").executeUpdate();
        }
    }

    private void insertTestData() {
        try (var connection = sql2o.open()) {
            connection.createQuery("""
                INSERT INTO users (full_name, email, password) VALUES
                ('John Doe', 'john.doe@example.com', 'password123'),
                ('Jane Smith', 'jane.smith@example.com', 'qwerty456'),
                ('Bob Johnson', 'bob.johnson@example.com', 'secret789')
            """).executeUpdate();
        }
    }

    private void clearTables() {
        try (var connection = sql2o.open()) {
            connection.createQuery("DELETE FROM users").executeUpdate();
        }
    }

    @Test
    /**
 * Тестирует сценарий: корректность возвращаемых данных при save new user_then returns user with id
 * 
 * @see #whenSaveNewUser_thenReturnsUserWithId()
 */
    void whenSaveNewUser_thenReturnsUserWithId() {
        var user = new User(0, "Alice Brown", "alice.brown@example.com", "alicepass");

        Optional<User> savedUser = userRepository.save(user);

        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getId()).isGreaterThan(0);
        assertThat(savedUser.get().getFullName()).isEqualTo("Alice Brown");
        assertThat(savedUser.get().getEmail()).isEqualTo("alice.brown@example.com");
        assertThat(savedUser.get().getPassword()).isEqualTo("alicepass");
    }

    @Test
    /**
 * Тестирует сценарий: обработку исключения при save user with existing email_then throw exception
 * 
 * @see #whenSaveUserWithExistingEmail_thenThrowException()
 */
    void whenSaveUserWithExistingEmail_thenThrowException() {
        var user = new User(0, "Duplicate User", "john.doe@example.com", "differentpass");

        assertThatThrownBy(() -> userRepository.save(user))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Unique index or primary key violation");
    }

    @Test
    /**
 * Тестирует сценарий: успешное выполнение при find by email and password exists_then return user
 * 
 * @see #whenFindByEmailAndPasswordExists_thenReturnUser()
 */
    void whenFindByEmailAndPasswordExists_thenReturnUser() {
        Optional<User> found = userRepository.findByEmailAndPassword("john.doe@example.com", "password123");

        assertThat(found).isPresent();
        User user = found.get();
        assertThat(user.getId()).isEqualTo(1);
        assertThat(user.getFullName()).isEqualTo("John Doe");
        assertThat(user.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(user.getPassword()).isEqualTo("password123");
    }

    @Test
    /**
 * Тестирует сценарий: поведение с пустыми данными при find by email and password wrong email_then return empty
 * 
 * @see #whenFindByEmailAndPasswordWrongEmail_thenReturnEmpty()
 */
    void whenFindByEmailAndPasswordWrongEmail_thenReturnEmpty() {
        Optional<User> found = userRepository.findByEmailAndPassword("wrong@example.com", "password123");
        assertThat(found).isEmpty();
    }

    @Test
    /**
 * Тестирует сценарий: поведение с пустыми данными при find by email and password wrong password_then return empty
 * 
 * @see #whenFindByEmailAndPasswordWrongPassword_thenReturnEmpty()
 */
    void whenFindByEmailAndPasswordWrongPassword_thenReturnEmpty() {
        Optional<User> found = userRepository.findByEmailAndPassword("john.doe@example.com", "wrongpassword");
        assertThat(found).isEmpty();
    }

    @Test
    /**
 * Тестирует сценарий: поведение с пустыми данными при find by email and password both wrong_then return empty
 * 
 * @see #whenFindByEmailAndPasswordBothWrong_thenReturnEmpty()
 */
    void whenFindByEmailAndPasswordBothWrong_thenReturnEmpty() {
        Optional<User> found = userRepository.findByEmailAndPassword("wrong@example.com", "wrongpassword");
        assertThat(found).isEmpty();
    }

    @Test
    /**
 * Тестирует сценарий: save multiple users_then all have unique ids
 * 
 * @see #whenSaveMultipleUsers_thenAllHaveUniqueIds()
 */
    void whenSaveMultipleUsers_thenAllHaveUniqueIds() {
        var user1 = userRepository.save(new User(0, "User One", "user1@example.com", "pass1"));
        var user2 = userRepository.save(new User(0, "User Two", "user2@example.com", "pass2"));
        var user3 = userRepository.save(new User(0, "User Three", "user3@example.com", "pass3"));

        assertThat(user1).isPresent();
        assertThat(user2).isPresent();
        assertThat(user3).isPresent();

        assertThat(user1.get().getId()).isNotEqualTo(user2.get().getId());
        assertThat(user2.get().getId()).isNotEqualTo(user3.get().getId());
        assertThat(user1.get().getId()).isNotEqualTo(user3.get().getId());
    }

    @Test
    /**
 * Тестирует сценарий: find by email and password_then case sensitive
 * 
 * @see #whenFindByEmailAndPassword_thenCaseSensitive()
 */
    void whenFindByEmailAndPassword_thenCaseSensitive() {
        Optional<User> foundLowercase = userRepository.findByEmailAndPassword("john.doe@example.com", "password123");
        Optional<User> foundUppercase = userRepository.findByEmailAndPassword("JOHN.DOE@EXAMPLE.COM", "password123");

        assertThat(foundLowercase).isPresent();
        assertThat(foundUppercase).isEmpty();
    }

    @Test
    /**
 * Тестирует сценарий: save user_then can find by email and password
 * 
 * @see #whenSaveUser_thenCanFindByEmailAndPassword()
 */
    void whenSaveUser_thenCanFindByEmailAndPassword() {
        var newUser = new User(0, "New User", "new.user@example.com", "newpass123");
        userRepository.save(newUser);

        Optional<User> found = userRepository.findByEmailAndPassword("new.user@example.com", "newpass123");

        assertThat(found).isPresent();
        assertThat(found.get().getFullName()).isEqualTo("New User");
        assertThat(found.get().getEmail()).isEqualTo("new.user@example.com");
        assertThat(found.get().getPassword()).isEqualTo("newpass123");
    }

    @Test
    /**
 * Тестирует сценарий: поведение с пустыми данными при save user with empty fields_then success
 * 
 * @see #whenSaveUserWithEmptyFields_thenSuccess()
 */
    void whenSaveUserWithEmptyFields_thenSuccess() {
        var user = new User(0, "", "empty@example.com", "");

        Optional<User> savedUser = userRepository.save(user);

        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getId()).isGreaterThan(0);
        assertThat(savedUser.get().getFullName()).isEmpty();
        assertThat(savedUser.get().getPassword()).isEmpty();
    }

    @Test
    /**
 * Тестирует сценарий: поведение с пустыми данными при find by email and password with null values_then return empty
 * 
 * @see #whenFindByEmailAndPasswordWithNullValues_thenReturnEmpty()
 */
    void whenFindByEmailAndPasswordWithNullValues_thenReturnEmpty() {
        Optional<User> foundNullEmail = userRepository.findByEmailAndPassword(null, "password123");
        Optional<User> foundNullPassword = userRepository.findByEmailAndPassword("john.doe@example.com", null);
        Optional<User> foundBothNull = userRepository.findByEmailAndPassword(null, null);

        assertThat(foundNullEmail).isEmpty();
        assertThat(foundNullPassword).isEmpty();
        assertThat(foundBothNull).isEmpty();
    }

    @Test
    /**
 * Тестирует сценарий: user properties are accessible_then success
 * 
 * @see #whenUserPropertiesAreAccessible_thenSuccess()
 */
    void whenUserPropertiesAreAccessible_thenSuccess() {
        Optional<User> user = userRepository.findByEmailAndPassword("john.doe@example.com", "password123");

        assertThat(user).isPresent();
        User userObj = user.get();

        assertThat(userObj.getId()).isPositive();
        assertThat(userObj.getFullName()).isEqualTo("John Doe");
        assertThat(userObj.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(userObj.getPassword()).isEqualTo("password123");

        userObj.setFullName("Updated Name");
        userObj.setEmail("updated@example.com");
        userObj.setPassword("newpassword");

        assertThat(userObj.getFullName()).isEqualTo("Updated Name");
        assertThat(userObj.getEmail()).isEqualTo("updated@example.com");
        assertThat(userObj.getPassword()).isEqualTo("newpassword");
    }

    @Test
    /**
 * Тестирует сценарий: поведение с пустыми данными при database is empty_then find by email returns empty
 * 
 * @see #whenDatabaseIsEmpty_thenFindByEmailReturnsEmpty()
 */
    void whenDatabaseIsEmpty_thenFindByEmailReturnsEmpty() {
        clearTables();

        Optional<User> found = userRepository.findByEmailAndPassword("john.doe@example.com", "password123");
        assertThat(found).isEmpty();
    }

    @Test
    /**
 * Тестирует сценарий: save and find_then data consistent
 * 
 * @see #whenSaveAndFind_thenDataConsistent()
 */
    void whenSaveAndFind_thenDataConsistent() {
        var originalUser = new User(0, "Test User", "test@example.com", "testpass");

        Optional<User> saved = userRepository.save(originalUser);
        assertThat(saved).isPresent();

        Optional<User> found = userRepository.findByEmailAndPassword("test@example.com", "testpass");
        assertThat(found).isPresent();

        assertThat(found.get().getId()).isEqualTo(saved.get().getId());
        assertThat(found.get().getFullName()).isEqualTo(saved.get().getFullName());
        assertThat(found.get().getEmail()).isEqualTo(saved.get().getEmail());
        assertThat(found.get().getPassword()).isEqualTo(saved.get().getPassword());
    }

    @Test
    /**
 * Тестирует сценарий: save after pre inserted data_then id continues sequence
 * 
 * @see #whenSaveAfterPreInsertedData_thenIdContinuesSequence()
 */
    void whenSaveAfterPreInsertedData_thenIdContinuesSequence() {
        var newUser = userRepository.save(new User(0, "Fourth User", "fourth@example.com", "pass4"));

        assertThat(newUser).isPresent();
        assertThat(newUser.get().getId()).isEqualTo(4);
    }

    @Test
    /**
 * Тестирует сценарий: save user with long data_then success
 * 
 * @see #whenSaveUserWithLongData_thenSuccess()
 */
    void whenSaveUserWithLongData_thenSuccess() {
        String longName = "A".repeat(100);
        String longEmail = "email@" + "a".repeat(240) + ".com";
        String longPassword = "P".repeat(100);

        var user = new User(0, longName, longEmail, longPassword);

        Optional<User> savedUser = userRepository.save(user);

        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getFullName()).isEqualTo(longName);
        assertThat(savedUser.get().getEmail()).isEqualTo(longEmail);
        assertThat(savedUser.get().getPassword()).isEqualTo(longPassword);
    }
}