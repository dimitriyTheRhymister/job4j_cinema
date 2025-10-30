package ru.job4j.cinema.controller;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.cinema.dto.FileDto;
import ru.job4j.cinema.service.FileService;

import java.io.ByteArrayInputStream;
import java.util.Optional;

@Controller
@RequestMapping("/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<InputStreamResource> getFile(@PathVariable int id) {
        Optional<FileDto> fileDto = fileService.getFileById(id);
        if (fileDto.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var content = fileDto.get().getContent();
        var inputStream = new ByteArrayInputStream(content);
        var resource = new InputStreamResource(inputStream);
        return ResponseEntity.ok()
                .contentLength(content.length)
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }
}