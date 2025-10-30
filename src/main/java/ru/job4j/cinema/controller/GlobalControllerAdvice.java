package ru.job4j.cinema.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("currentPage")
    public String getCurrentPage(HttpServletRequest request) {
        String path = request.getServletPath();

        if (path.equals("/") || path.equals("/index")) {
            return "home";
        } else if (path.startsWith("/films")) {
            return "films";
        } else if (path.startsWith("/sessions")) {
            return "sessions";
        } else if (path.startsWith("/halls")) {
            return "halls";
        } else if (path.startsWith("/tickets")) {
            return "tickets";
        } else if (path.startsWith("/users")) {
            return "users";
        } else if (path.startsWith("/genres")) {
            return "genres";
        }

        return "";
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public String handleResourceNotFound(NoResourceFoundException ex, Model model) {
        model.addAttribute("message", "Страница не найдена");
        return "errors/404";
    }

    @ExceptionHandler(Exception.class)
    public String handleAllExceptions(Exception ex, Model model) {
        model.addAttribute("message", "Произошла ошибка: " + ex.getMessage());
        return "errors/404";
    }
}