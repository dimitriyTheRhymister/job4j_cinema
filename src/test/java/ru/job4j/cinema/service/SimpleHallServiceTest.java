package ru.job4j.cinema.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.repository.hall.HallRepository;
import ru.job4j.cinema.service.hall.SimpleHallService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SimpleHallServiceTest {

    @Mock
    private HallRepository hallRepository;

    @InjectMocks
    private SimpleHallService hallService;

    /**
 * Тестирует сценарий: успешное выполнение при find by id_ when hall exists_ should return hall
 * 
 * @see #findById_WhenHallExists_ShouldReturnHall()
 */


    @Test


    void findById_WhenHallExists_ShouldReturnHall() {
        Hall expectedHall = new Hall(1, "Main Hall", 10, 15, "Main cinema hall");
        when(hallRepository.findById(1)).thenReturn(Optional.of(expectedHall));

        Optional<Hall> result = hallService.findById(1);

        assertTrue(result.isPresent());
        assertEquals(expectedHall, result.get());
        verify(hallRepository, times(1)).findById(1);
    }

    /**
 * Тестирует сценарий: успешное выполнение при find by id_ when hall not exists_ should return empty
 * 
 * @see #findById_WhenHallNotExists_ShouldReturnEmpty()
 */


    @Test


    void findById_WhenHallNotExists_ShouldReturnEmpty() {
        when(hallRepository.findById(1)).thenReturn(Optional.empty());

        Optional<Hall> result = hallService.findById(1);

        assertTrue(result.isEmpty());
        verify(hallRepository, times(1)).findById(1);
    }

    /**
 * Тестирует сценарий: корректность возвращаемых данных при find by id_ should return same optional from repository
 * 
 * @see #findById_ShouldReturnSameOptionalFromRepository()
 */


    @Test


    void findById_ShouldReturnSameOptionalFromRepository() {
        Hall hall = new Hall(1, "Main Hall", 10, 15, "Main hall");
        Optional<Hall> repositoryOptional = Optional.of(hall);

        when(hallRepository.findById(1)).thenReturn(repositoryOptional);

        Optional<Hall> result = hallService.findById(1);

        assertThat(result).isSameAs(repositoryOptional);
        verify(hallRepository, times(1)).findById(1);
    }

    /**
 * Тестирует сценарий: обработку ошибки при find by id_ with invalid ids_ should return empty
 * 
 * @see #findById_WithInvalidIds_ShouldReturnEmpty()
 */


    @Test


    void findById_WithInvalidIds_ShouldReturnEmpty() {
        assertThat(hallService.findById(0)).isEmpty();
        assertThat(hallService.findById(-1)).isEmpty();

        verify(hallRepository, times(1)).findById(0);
        verify(hallRepository, times(1)).findById(-1);
    }

    /**
 * Тестирует сценарий: успешное выполнение при find by id_ when hall exists_ should return complete hall data
 * 
 * @see #findById_WhenHallExists_ShouldReturnCompleteHallData()
 */


    @Test


    void findById_WhenHallExists_ShouldReturnCompleteHallData() {
        Hall expectedHall = new Hall(1, "VIP Hall", 5, 8, "VIP hall with premium seats");
        when(hallRepository.findById(1)).thenReturn(Optional.of(expectedHall));

        Optional<Hall> result = hallService.findById(1);

        assertThat(result).isPresent();
        Hall hall = result.get();
        assertThat(hall.getId()).isEqualTo(1);
        assertThat(hall.getName()).isEqualTo("VIP Hall");
        assertThat(hall.getRowCount()).isEqualTo(5);
        assertThat(hall.getPlaceCount()).isEqualTo(8);
        assertThat(hall.getDescription()).isEqualTo("VIP hall with premium seats");
    }
}