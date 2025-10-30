package ru.job4j.cinema.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.UserService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest {

    private UserService userService;
    private UserController userController;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    /**
 * Тестирует сценарий: корректность возвращаемых данных при get register page then return register view
 * 
 * @see #whenGetRegisterPageThenReturnRegisterView()
 */
    void whenGetRegisterPageThenReturnRegisterView() throws Exception {
        mockMvc.perform(get("/users/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/register"));
    }

    @Test
    /**
 * Тестирует сценарий: register with new user then redirect to login
 * 
 * @see #whenRegisterWithNewUserThenRedirectToLogin()
 */
    void whenRegisterWithNewUserThenRedirectToLogin() throws Exception {
        User user = new User(1, "Test User", "test@example.com", "password");
        when(userService.save(any(User.class))).thenReturn(Optional.of(user));

        mockMvc.perform(post("/users/register")
                        .param("fullName", "Test User")
                        .param("email", "test@example.com")
                        .param("password", "password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/login"));

        verify(userService).save(any(User.class));
    }

    @Test
    /**
 * Тестирует сценарий: обработку исключения при register with existing email then return error page
 * 
 * @see #whenRegisterWithExistingEmailThenReturnErrorPage()
 */
    void whenRegisterWithExistingEmailThenReturnErrorPage() throws Exception {
        when(userService.save(any(User.class))).thenReturn(Optional.empty());

        mockMvc.perform(post("/users/register")
                        .param("fullName", "Test User")
                        .param("email", "existing@example.com")
                        .param("password", "password"))
                .andExpect(status().isOk())
                .andExpect(view().name("errors/404"))
                .andExpect(model().attribute("message", "Пользователь с таким email уже существует"));

        verify(userService).save(any(User.class));
    }

    @Test
    /**
 * Тестирует сценарий: обработку исключения при register with duplicate email exception then return error page
 * 
 * @see #whenRegisterWithDuplicateEmailExceptionThenReturnErrorPage()
 */
    void whenRegisterWithDuplicateEmailExceptionThenReturnErrorPage() throws Exception {
        when(userService.save(any(User.class)))
                .thenThrow(new RuntimeException("users_email_key violation"));

        mockMvc.perform(post("/users/register")
                        .param("fullName", "Test User")
                        .param("email", "duplicate@example.com")
                        .param("password", "password"))
                .andExpect(status().isOk())
                .andExpect(view().name("errors/404"))
                .andExpect(model().attribute("message", "Пользователь с таким email уже существует."));

        verify(userService).save(any(User.class));
    }

    @Test
    /**
 * Тестирует сценарий: обработку исключения при register with general exception then return error page
 * 
 * @see #whenRegisterWithGeneralExceptionThenReturnErrorPage()
 */
    void whenRegisterWithGeneralExceptionThenReturnErrorPage() throws Exception {
        when(userService.save(any(User.class)))
                .thenThrow(new RuntimeException("Database connection failed"));

        mockMvc.perform(post("/users/register")
                        .param("fullName", "Test User")
                        .param("email", "test@example.com")
                        .param("password", "password"))
                .andExpect(status().isOk())
                .andExpect(view().name("errors/404"))
                .andExpect(model().attribute("message", "Произошла ошибка при регистрации."));

        verify(userService).save(any(User.class));
    }

    @Test
    /**
 * Тестирует сценарий: корректность возвращаемых данных при get login page then return login view
 * 
 * @see #whenGetLoginPageThenReturnLoginView()
 */
    void whenGetLoginPageThenReturnLoginView() throws Exception {
        mockMvc.perform(get("/users/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/login"));
    }

    @Test
    /**
 * Тестирует сценарий: успешное выполнение при login with valid credentials then redirect to home
 * 
 * @see #whenLoginWithValidCredentialsThenRedirectToHome()
 */
    void whenLoginWithValidCredentialsThenRedirectToHome() throws Exception {
        User user = new User(1, "Test User", "test@example.com", "password");
        when(userService.findByEmailAndPassword("test@example.com", "password"))
                .thenReturn(Optional.of(user));

        mockMvc.perform(post("/users/login")
                        .param("email", "test@example.com")
                        .param("password", "password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(userService).findByEmailAndPassword("test@example.com", "password");
    }

    @Test
    /**
 * Тестирует сценарий: обработку ошибки при login with invalid credentials then return login with error
 * 
 * @see #whenLoginWithInvalidCredentialsThenReturnLoginWithError()
 */
    void whenLoginWithInvalidCredentialsThenReturnLoginWithError() throws Exception {
        when(userService.findByEmailAndPassword("wrong@example.com", "wrong"))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/users/login")
                        .param("email", "wrong@example.com")
                        .param("password", "wrong"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/login"))
                .andExpect(model().attribute("error", "Неверный email или пароль"));

        verify(userService).findByEmailAndPassword("wrong@example.com", "wrong");
    }

    @Test
    /**
 * Тестирует сценарий: поведение с пустыми данными при login with empty credentials then return login with error
 * 
 * @see #whenLoginWithEmptyCredentialsThenReturnLoginWithError()
 */
    void whenLoginWithEmptyCredentialsThenReturnLoginWithError() throws Exception {
        mockMvc.perform(post("/users/login")
                        .param("email", "")
                        .param("password", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("users/login"))
                .andExpect(model().attributeExists("error"));

        verify(userService).findByEmailAndPassword("", "");
    }

    @Test
    /**
 * Тестирует сценарий: logout get then redirect to home
 * 
 * @see #whenLogoutGetThenRedirectToHome()
 */
    void whenLogoutGetThenRedirectToHome() throws Exception {
        mockMvc.perform(get("/users/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    /**
 * Тестирует сценарий: поведение с пустыми данными при register with empty data then handle gracefully
 * 
 * @see #whenRegisterWithEmptyDataThenHandleGracefully()
 */
    void whenRegisterWithEmptyDataThenHandleGracefully() throws Exception {
        mockMvc.perform(post("/users/register")
                        .param("fullName", "")
                        .param("email", "")
                        .param("password", ""))
                .andExpect(status().isOk());

        verify(userService).save(any(User.class));
    }
}