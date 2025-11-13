package ru.job4j.cinema.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.user.UserService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserControllerTest {

    private UserService userService;
    private UserController userController;
    private HttpServletRequest request;
    private HttpSession session;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
        request = mock(HttpServletRequest.class);
        session = mock(HttpSession.class);
    }

    @Test
    void whenRegisterWithDuplicateEmailThenReturnErrorPage() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setFullName("Test User");

        when(userService.save(any(User.class))).thenReturn(Optional.empty());
        when(request.getSession(false)).thenReturn(session);

        Model model = new ConcurrentModel();
        String result = userController.register(user, model, request);

        assertThat(result).isEqualTo("errors/404");
        assertThat(model.getAttribute("message")).isEqualTo("Пользователь с таким email уже существует");
        verify(session).invalidate();
    }

    @Test
    void whenRegisterSuccessThenRedirectToLogin() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setFullName("Test User");

        User savedUser = new User();
        savedUser.setId(1);
        savedUser.setEmail("test@example.com");
        savedUser.setPassword("password");
        savedUser.setFullName("Test User");

        when(userService.save(any(User.class))).thenReturn(Optional.of(savedUser));
        when(request.getSession(false)).thenReturn(session);

        Model model = new ConcurrentModel();
        String result = userController.register(user, model, request);

        assertThat(result).isEqualTo("redirect:/users/login");
        verify(session).invalidate();
    }

    @Test
    void whenRegisterWithNoExistingSessionThenProceed() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setFullName("Test User");

        User savedUser = new User();
        savedUser.setId(1);
        savedUser.setEmail("test@example.com");
        savedUser.setPassword("password");
        savedUser.setFullName("Test User");

        when(userService.save(any(User.class))).thenReturn(Optional.of(savedUser));
        when(request.getSession(false)).thenReturn(null); // Нет активной сессии

        Model model = new ConcurrentModel();
        String result = userController.register(user, model, request);

        assertThat(result).isEqualTo("redirect:/users/login");
        // Не должно быть вызова invalidate, так как сессии не было
        verify(session, never()).invalidate();
    }

    @Test
    void whenLoginSuccessThenRedirectToHome() {
        String email = "test@example.com";
        String password = "password";

        User user = new User();
        user.setId(1);
        user.setEmail(email);
        user.setPassword(password);
        user.setFullName("Test User");

        when(userService.findByEmailAndPassword(email, password)).thenReturn(Optional.of(user));
        when(request.getSession()).thenReturn(session);

        Model model = new ConcurrentModel();
        String result = userController.login(email, password, model, request);

        assertThat(result).isEqualTo("redirect:/");
        verify(session).setAttribute("user", user);
    }

    @Test
    void whenLoginFailedThenReturnLoginPageWithError() {
        String email = "test@example.com";
        String password = "wrongpassword";

        when(userService.findByEmailAndPassword(email, password)).thenReturn(Optional.empty());

        Model model = new ConcurrentModel();
        String result = userController.login(email, password, model, request);

        assertThat(result).isEqualTo("users/login");
        assertThat(model.getAttribute("error")).isEqualTo("Неверный email или пароль");
        // Не должно быть взаимодействия с сессией при неудачном логине
        verify(request, never()).getSession();
    }

    @Test
    void whenGetRegisterPageThenReturnRegisterPage() {
        String result = userController.getRegisterPage();
        assertThat(result).isEqualTo("users/register");
    }

    @Test
    void whenGetLoginPageThenReturnLoginPage() {
        String result = userController.getLoginPage();
        assertThat(result).isEqualTo("users/login");
    }

    @Test
    void whenLogoutWithSessionThenInvalidateAndRedirect() {
        when(request.getSession(false)).thenReturn(session);

        String result = userController.logout(request);

        assertThat(result).isEqualTo("redirect:/");
        verify(session).invalidate();
    }

    @Test
    void whenLogoutWithoutSessionThenRedirect() {
        when(request.getSession(false)).thenReturn(null);

        String result = userController.logout(request);

        assertThat(result).isEqualTo("redirect:/");
        // Не должно быть вызова invalidate, так как сессии не было
        verify(session, never()).invalidate();
    }

    @Test
    void whenRegisterWithDuplicateEmailThenReturnErrorPageWithGuestUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setFullName("Test User");

        when(userService.save(any(User.class))).thenReturn(Optional.empty());
        when(request.getSession(false)).thenReturn(session);

        Model model = new ConcurrentModel();
        String result = userController.register(user, model, request);

        assertThat(result).isEqualTo("errors/404");
        assertThat(model.getAttribute("message")).isEqualTo("Пользователь с таким email уже существует");

        // Проверяем, что в модель добавлен гость
        User guestUser = (User) model.getAttribute("user");
        assertThat(guestUser).isNotNull();
        assertThat(guestUser.getFullName()).isEqualTo("Гость");

        verify(session).invalidate();
    }
}