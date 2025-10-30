package ru.job4j.cinema.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IndexControllerTest {

    private final IndexController indexController = new IndexController();

    @Test
    /**
 * Тестирует сценарий: корректность возвращаемых данных при get index_then return index view
 * 
 * @see #whenGetIndex_thenReturnIndexView()
 */
    void whenGetIndex_thenReturnIndexView() {
        String viewName = indexController.getIndex();
        assertEquals("index", viewName);
    }
}