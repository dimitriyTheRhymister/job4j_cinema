package ru.job4j.cinema.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.repository.user.UserRepository;
import ru.job4j.cinema.service.user.SimpleUserService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SimpleUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SimpleUserService userService;

    @Test
    /**
 * Тестирует сценарий: успешное выполнение при save_ when user is valid_ should return saved user
 * 
 * @see #save_WhenUserIsValid_ShouldReturnSavedUser()
 */
    void save_WhenUserIsValid_ShouldReturnSavedUser() {
        User newUser = new User(0, "John Doe", "john@example.com", "password123");
        User savedUser = new User(1, "John Doe", "john@example.com", "password123");

        when(userRepository.save(newUser)).thenReturn(Optional.of(savedUser));

        Optional<User> result = userService.save(newUser);

        assertTrue(result.isPresent());
        assertEquals(savedUser, result.get());
        assertEquals(1, result.get().getId());
        verify(userRepository, times(1)).save(newUser);
    }

    @Test
    /**
 * Тестирует сценарий: поведение с пустыми данными при save_ when user save fails_ should return empty
 * 
 * @see #save_WhenUserSaveFails_ShouldReturnEmpty()
 */
    void save_WhenUserSaveFails_ShouldReturnEmpty() {
        User newUser = new User(0, "John Doe", "john@example.com", "password123");

        when(userRepository.save(newUser)).thenReturn(Optional.empty());

        Optional<User> result = userService.save(newUser);

        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).save(newUser);
    }

    @Test
    /**
 * Тестирует сценарий: поведение с пустыми данными при save_ when user is null_ should call repository with null
 * 
 * @see #save_WhenUserIsNull_ShouldCallRepositoryWithNull()
 */
    void save_WhenUserIsNull_ShouldCallRepositoryWithNull() {
        when(userRepository.save(null)).thenReturn(Optional.empty());

        Optional<User> result = userService.save(null);

        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).save(null);
    }

    @Test
    /**
 * Тестирует сценарий: успешное выполнение при find by email and password_ when user exists_ should return user
 * 
 * @see #findByEmailAndPassword_WhenUserExists_ShouldReturnUser()
 */
    void findByEmailAndPassword_WhenUserExists_ShouldReturnUser() {
        User expectedUser = new User(1, "John Doe", "john@example.com", "password123");

        when(userRepository.findByEmailAndPassword("john@example.com", "password123"))
                .thenReturn(Optional.of(expectedUser));

        Optional<User> result = userService.findByEmailAndPassword("john@example.com", "password123");

        assertTrue(result.isPresent());
        assertEquals(expectedUser, result.get());
        verify(userRepository, times(1)).findByEmailAndPassword("john@example.com", "password123");
    }

    @Test
    /**
 * Тестирует сценарий: успешное выполнение при find by email and password_ when user not exists_ should return empty
 * 
 * @see #findByEmailAndPassword_WhenUserNotExists_ShouldReturnEmpty()
 */
    void findByEmailAndPassword_WhenUserNotExists_ShouldReturnEmpty() {
        when(userRepository.findByEmailAndPassword("nonexistent@example.com", "wrongpassword"))
                .thenReturn(Optional.empty());

        Optional<User> result = userService.findByEmailAndPassword("nonexistent@example.com", "wrongpassword");

        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findByEmailAndPassword("nonexistent@example.com", "wrongpassword");
    }

    @Test
    /**
 * Тестирует сценарий: поведение с пустыми данными при find by email and password_ with null credentials_ should return empty
 * 
 * @see #findByEmailAndPassword_WithNullCredentials_ShouldReturnEmpty()
 */
    void findByEmailAndPassword_WithNullCredentials_ShouldReturnEmpty() {
        when(userRepository.findByEmailAndPassword(null, null)).thenReturn(Optional.empty());

        Optional<User> result = userService.findByEmailAndPassword(null, null);

        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findByEmailAndPassword(null, null);
    }

    @Test
    /**
 * Тестирует сценарий: поведение с пустыми данными при find by email and password_ with empty credentials_ should return empty
 * 
 * @see #findByEmailAndPassword_WithEmptyCredentials_ShouldReturnEmpty()
 */
    void findByEmailAndPassword_WithEmptyCredentials_ShouldReturnEmpty() {
        when(userRepository.findByEmailAndPassword("", "")).thenReturn(Optional.empty());

        Optional<User> result = userService.findByEmailAndPassword("", "");

        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findByEmailAndPassword("", "");
    }

    @Test
    /**
 * Тестирует сценарий: корректность возвращаемых данных при save_ should return user with generated id
 * 
 * @see #save_ShouldReturnUserWithGeneratedId()
 */
    void save_ShouldReturnUserWithGeneratedId() {
        User newUser = new User(0, "Jane Smith", "jane@example.com", "securepass");
        User savedUser = new User(2, "Jane Smith", "jane@example.com", "securepass");

        when(userRepository.save(newUser)).thenReturn(Optional.of(savedUser));

        Optional<User> result = userService.save(newUser);

        assertTrue(result.isPresent());
        assertEquals(2, result.get().getId());
        assertEquals("Jane Smith", result.get().getFullName());
        assertEquals("jane@example.com", result.get().getEmail());
        assertEquals("securepass", result.get().getPassword());
        verify(userRepository, times(1)).save(newUser);
    }

    @Test
    /**
 * Тестирует сценарий: find by email and password_ should be case sensitive
 * 
 * @see #findByEmailAndPassword_ShouldBeCaseSensitive()
 */
    void findByEmailAndPassword_ShouldBeCaseSensitive() {
        User user = new User(1, "John Doe", "john@example.com", "password123");

        when(userRepository.findByEmailAndPassword("JOHN@example.com", "PASSWORD123"))
                .thenReturn(Optional.empty());
        when(userRepository.findByEmailAndPassword("john@example.com", "password123"))
                .thenReturn(Optional.of(user));

        Optional<User> wrongCase = userService.findByEmailAndPassword("JOHN@example.com", "PASSWORD123");
        Optional<User> correctCase = userService.findByEmailAndPassword("john@example.com", "password123");

        assertTrue(wrongCase.isEmpty());
        assertTrue(correctCase.isPresent());

        verify(userRepository, times(1)).findByEmailAndPassword("JOHN@example.com", "PASSWORD123");
        verify(userRepository, times(1)).findByEmailAndPassword("john@example.com", "password123");
    }

    @Test
    /**
 * Тестирует сценарий: поведение с пустыми данными при save_ with duplicate email_ should return empty
 * 
 * @see #save_WithDuplicateEmail_ShouldReturnEmpty()
 */
    void save_WithDuplicateEmail_ShouldReturnEmpty() {
        User duplicateUser = new User(0, "Another User", "john@example.com", "differentpass");

        when(userRepository.save(duplicateUser)).thenReturn(Optional.empty());

        Optional<User> result = userService.save(duplicateUser);

        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).save(duplicateUser);
    }

    @Test
    /**
 * Тестирует сценарий: methods_ should directly delegate to repository
 * 
 * @see #methods_ShouldDirectlyDelegateToRepository()
 */
    void methods_ShouldDirectlyDelegateToRepository() {
        User user = new User(0, "Test User", "test@example.com", "testpass");
        User savedUser = new User(1, "Test User", "test@example.com", "testpass");

        when(userRepository.save(user)).thenReturn(Optional.of(savedUser));
        when(userRepository.findByEmailAndPassword("test@example.com", "testpass"))
                .thenReturn(Optional.of(savedUser));

        Optional<User> saveResult = userService.save(user);
        Optional<User> findResult = userService.findByEmailAndPassword("test@example.com", "testpass");

        assertTrue(saveResult.isPresent());
        assertTrue(findResult.isPresent());
        assertEquals(savedUser, saveResult.get());
        assertEquals(savedUser, findResult.get());
    }
}