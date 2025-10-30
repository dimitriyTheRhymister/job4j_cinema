package ru.job4j.cinema.service;

import net.jcip.annotations.ThreadSafe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.dto.FileDto;
import ru.job4j.cinema.model.File;
import ru.job4j.cinema.repository.FileRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@ThreadSafe
@Service
public class SimpleFileService implements FileService {

    private final FileRepository fileRepository;

    @Value("${app.files.path:files/images/}")
    private String uploadPath;

    public SimpleFileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Override
    public File save(FileDto fileDto) {
        String fileName = UUID.randomUUID().toString() + "_" + fileDto.getName();
        String filePath = uploadPath + fileName;

        Path dirPath = Paths.get(uploadPath);
        try {
            Files.createDirectories(dirPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create directory: " + uploadPath, e);
        }

        Path fullPath = Paths.get(filePath);
        try {
            Files.write(fullPath, fileDto.getContent());
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file", e);
        }

        File file = new File();
        file.setName(fileName);
        file.setPath(filePath);
        return fileRepository.save(file);
    }

    @Override
    public Optional<FileDto> getFileById(int id) {
        var fileOptional = fileRepository.findById(id);
        if (fileOptional.isPresent()) {
            var file = fileOptional.get();
            Path fullPath = Paths.get(file.getPath());
            try {
                byte[] content = Files.readAllBytes(fullPath);
                return Optional.of(new FileDto(file.getName(), content));
            } catch (IOException e) {
                throw new RuntimeException("Failed to read file", e);
            }
        }
        return Optional.empty();
    }

    void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }
}