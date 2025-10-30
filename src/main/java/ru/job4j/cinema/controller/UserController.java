package ru.job4j.cinema.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.UserService;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String getRegisterPage() {
        return "users/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user, Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        User guest = new User();
        guest.setFullName("Гость");
        model.addAttribute("user", guest);

        try {
            var savedUser = userService.save(user);
            if (savedUser.isEmpty()) {
                model.addAttribute("message", "Пользователь с таким email уже существует");
                return "errors/404";
            }
            return "redirect:/users/login";
        } catch (Exception exception) {
            if (exception.getMessage().contains("users_email_key")) {
                model.addAttribute("message", "Пользователь с таким email уже существует.");
            } else {
                model.addAttribute("message", "Произошла ошибка при регистрации.");
            }
            return "errors/404";
        }
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "users/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password, Model model, HttpServletRequest request) {
        var userOptional = userService.findByEmailAndPassword(email, password);
        if (userOptional.isEmpty()) {
            model.addAttribute("error", "Неверный email или пароль");
            return "users/login";
        }
        HttpSession session = request.getSession();
        session.setAttribute("user", userOptional.get());

        return "redirect:/";
    }

    @GetMapping("/logout")
    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }
}