package ru.job4j.cinema.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.job4j.cinema.dto.FileDto;
import ru.job4j.cinema.service.file.FileService;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class FileControllerTest {

    private MockMvc mockMvc;
    private FileService fileService;

    @BeforeEach
    void setUp() {
        fileService = mock(FileService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new FileController(fileService)).build();
    }

    /**
 * Тестирует сценарий: успешное выполнение при get file exists_then return file with correct headers
 * 
 * @see #whenGetFileExists_thenReturnFileWithCorrectHeaders()
 */


    @Test


    void whenGetFileExists_thenReturnFileWithCorrectHeaders() throws Exception {
        byte[] testContent = "test image content".getBytes();
        FileDto fileDto = new FileDto("test.jpg", testContent);

        when(fileService.getFileById(1)).thenReturn(Optional.of(fileDto));

        mockMvc.perform(get("/files/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG))
                .andExpect(header().longValue("Content-Length", testContent.length))
                .andExpect(content().bytes(testContent));

        verify(fileService).getFileById(1);
    }

    /**
 * Тестирует сценарий: успешное выполнение при get file not exists_then return not found
 * 
 * @see #whenGetFileNotExists_thenReturnNotFound()
 */


    @Test


    void whenGetFileNotExists_thenReturnNotFound() throws Exception {
        when(fileService.getFileById(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/files/999"))
                .andExpect(status().isNotFound());

        verify(fileService).getFileById(999);
    }

    /**
 * Тестирует сценарий: поведение с пустыми данными при get file with empty content_then return empty file
 * 
 * @see #whenGetFileWithEmptyContent_thenReturnEmptyFile()
 */


    @Test


    void whenGetFileWithEmptyContent_thenReturnEmptyFile() throws Exception {
        byte[] emptyContent = new byte[0];
        FileDto fileDto = new FileDto("empty.jpg", emptyContent);

        when(fileService.getFileById(3)).thenReturn(Optional.of(fileDto));

        mockMvc.perform(get("/files/3"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG))
                .andExpect(header().longValue("Content-Length", 0))
                .andExpect(content().bytes(emptyContent));

        verify(fileService).getFileById(3);
    }

    /**
 * Тестирует сценарий: корректность возвращаемых данных при get file with negative id_then return not found
 * 
 * @see #whenGetFileWithNegativeId_thenReturnNotFound()
 */


    @Test


    void whenGetFileWithNegativeId_thenReturnNotFound() throws Exception {
        mockMvc.perform(get("/files/-1"))
                .andExpect(status().isNotFound());
    }

    /**
 * Тестирует сценарий: get file_then service called once
 * 
 * @see #whenGetFile_thenServiceCalledOnce()
 */


    @Test


    void whenGetFile_thenServiceCalledOnce() throws Exception {
        byte[] content = "content".getBytes();
        when(fileService.getFileById(5)).thenReturn(Optional.of(new FileDto("test.jpg", content)));

        mockMvc.perform(get("/files/5"));

        verify(fileService, times(1)).getFileById(5);
        verifyNoMoreInteractions(fileService);
    }
}