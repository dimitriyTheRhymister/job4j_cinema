package ru.job4j.cinema.repository.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.sql2o.Sql2o;
import ru.job4j.cinema.model.User;

import java.util.Optional;

@Repository
public class Sql2oUserRepository implements UserRepository {

    private static final Logger LOG = LoggerFactory.getLogger(Sql2oUserRepository.class);
    private final Sql2o sql2o;

    public Sql2oUserRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public Optional<User> save(User user) {
        try (var connection = sql2o.open()) {
            var sql = """
                    INSERT INTO users(full_name, email, password)
                    VALUES (:fullName, :email, :password)
                    """;
            var query = connection.createQuery(sql, true)
                    .addParameter("fullName", user.getFullName())
                    .addParameter("email", user.getEmail())
                    .addParameter("password", user.getPassword());
            int generatedId = query.executeUpdate().getKey(Integer.class);
            user.setId(generatedId);
            return Optional.of(user);
        } catch (Exception e) {
            LOG.error("Failed to save user: {}", user.getEmail(), e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByEmailAndPassword(String email, String password) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("SELECT * FROM users WHERE email = :email AND password = :password");
            query.addParameter("email", email).addParameter("password", password);
            var user = query.setColumnMappings(User.COLUMN_MAPPING).executeAndFetchFirst(User.class);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            LOG.error("Failed to find user by email and password: {}", email, e);
            return Optional.empty();
        }
    }
}