package ru.job4j.cinema.service.file;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.job4j.cinema.dto.FileDto;
import ru.job4j.cinema.model.File;
import ru.job4j.cinema.repository.file.FileRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SimpleFileServiceTest {

    @Mock
    private FileRepository fileRepository;

    private SimpleFileService fileService;

    @TempDir
    Path tempDir;

    private FileDto testFileDto;
    private File savedFile;

    @BeforeEach
    void setUp() {
        fileService = new SimpleFileService(fileRepository);
        fileService.setUploadPath(tempDir.toString() + "/");

        byte[] content = "test file content".getBytes();
        testFileDto = new FileDto("test.txt", content);

        savedFile = new File();
        savedFile.setId(1);
        savedFile.setName("uuid_test.txt");
        savedFile.setPath(tempDir + "/uuid_test.txt");
    }

    @Test
    /**
 * Тестирует сценарий: успешное выполнение при save_ when valid file dto_ should save file and return entity
 * 
 * @see #save_WhenValidFileDto_ShouldSaveFileAndReturnEntity()
 */
    void save_WhenValidFileDto_ShouldSaveFileAndReturnEntity() throws IOException {
        when(fileRepository.save(any(File.class))).thenAnswer(invocation -> {
            File fileArg = invocation.getArgument(0);
            fileArg.setId(savedFile.getId());
            return fileArg;
        });

        File result = fileService.save(testFileDto);

        assertNotNull(result);
        assertEquals(savedFile.getId(), result.getId());

        Path filePath = Path.of(result.getPath());
        assertTrue(Files.exists(filePath), "Файл должен существовать по пути: " + filePath);
        assertArrayEquals(testFileDto.getContent(), Files.readAllBytes(filePath));

        assertTrue(result.getName().contains("test.txt"));
        assertTrue(result.getName().startsWith(result.getName().split("_")[0]));

        verify(fileRepository, times(1)).save(any(File.class));
    }

    @Test
    /**
 * Тестирует сценарий: обработку исключения при save_ when directory creation fails_ should throw runtime exception
 * 
 * @see #save_WhenDirectoryCreationFails_ShouldThrowRuntimeException()
 */
    void save_WhenDirectoryCreationFails_ShouldThrowRuntimeException() {
        String invalidPath;
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            invalidPath = "Z:\\invalid\\path\\that\\does\\not\\exist\\and\\has\\invalid\\drive\\";
        } else {
            invalidPath = "/proc/self/invalid_cannot_create";
        }

        fileService.setUploadPath(invalidPath);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> fileService.save(testFileDto));

        assertTrue(exception.getMessage().contains("Failed to create directory"));
        verify(fileRepository, never()).save(any(ru.job4j.cinema.model.File.class));
    }

    @Test
    /**
 * Тестирует сценарий: успешное выполнение при get file by id_ when file exists_ should return file dto
 * 
 * @see #getFileById_WhenFileExists_ShouldReturnFileDto()
 */
    void getFileById_WhenFileExists_ShouldReturnFileDto() throws IOException {
        Files.write(Path.of(savedFile.getPath()), testFileDto.getContent());
        when(fileRepository.findById(1)).thenReturn(Optional.of(savedFile));

        Optional<FileDto> result = fileService.getFileById(1);

        assertTrue(result.isPresent());
        assertEquals(savedFile.getName(), result.get().getName());
        assertArrayEquals(testFileDto.getContent(), result.get().getContent());
        verify(fileRepository, times(1)).findById(1);
    }

    @Test
    /**
 * Тестирует сценарий: успешное выполнение при get file by id_ when file not exists in database_ should return empty
 * 
 * @see #getFileById_WhenFileNotExistsInDatabase_ShouldReturnEmpty()
 */
    void getFileById_WhenFileNotExistsInDatabase_ShouldReturnEmpty() {
        when(fileRepository.findById(1)).thenReturn(Optional.empty());

        Optional<FileDto> result = fileService.getFileById(1);

        assertTrue(result.isEmpty());
        verify(fileRepository, times(1)).findById(1);
    }

    @Test
    /**
 * Тестирует сценарий: успешное выполнение при get file by id_ when file exists but physical file missing_ should throw runtime exception
 * 
 * @see #getFileById_WhenFileExistsButPhysicalFileMissing_ShouldThrowRuntimeException()
 */
    void getFileById_WhenFileExistsButPhysicalFileMissing_ShouldThrowRuntimeException() {
        when(fileRepository.findById(1)).thenReturn(Optional.of(savedFile));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> fileService.getFileById(1));

        assertTrue(exception.getMessage().contains("Failed to read file"));
        verify(fileRepository, times(1)).findById(1);
    }

    @Test
    /**
 * Тестирует сценарий: get file by id_ with different ids_ should work correctly
 * 
 * @see #getFileById_WithDifferentIds_ShouldWorkCorrectly()
 */
    void getFileById_WithDifferentIds_ShouldWorkCorrectly() throws IOException {
        File anotherFile = new File();
        anotherFile.setId(2);
        anotherFile.setName("another.jpg");
        anotherFile.setPath(tempDir + "/another.jpg");

        byte[] anotherContent = "another content".getBytes();
        Files.write(Path.of(anotherFile.getPath()), anotherContent);

        when(fileRepository.findById(2)).thenReturn(Optional.of(anotherFile));

        Optional<FileDto> result = fileService.getFileById(2);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("another.jpg");
        assertThat(result.get().getContent()).isEqualTo(anotherContent);
    }

    @Test
    /**
 * Тестирует сценарий: save_ should generate unique file name
 * 
 * @see #save_ShouldGenerateUniqueFileName()
 */
    void save_ShouldGenerateUniqueFileName() {
        when(fileRepository.save(any(File.class))).thenReturn(savedFile);

        File result = fileService.save(testFileDto);

        assertNotNull(result.getName());
        assertTrue(result.getName().contains("_test.txt"));
        assertTrue(result.getPath().contains("test.txt"));
        verify(fileRepository, times(1)).save(any(File.class));
    }

    @Test
    /**
 * Тестирует сценарий: save_ multiple files_ should generate different names
 * 
 * @see #save_MultipleFiles_ShouldGenerateDifferentNames()
 */
    void save_MultipleFiles_ShouldGenerateDifferentNames() {
        File firstFile = new File();
        firstFile.setId(1);
        firstFile.setName("uuid1_test.txt");
        firstFile.setPath(tempDir + "/uuid1_test.txt");

        File secondFile = new File();
        secondFile.setId(2);
        secondFile.setName("uuid2_test.txt");
        secondFile.setPath(tempDir + "/uuid2_test.txt");

        when(fileRepository.save(any(File.class)))
                .thenReturn(firstFile)
                .thenReturn(secondFile);

        File result1 = fileService.save(testFileDto);
        File result2 = fileService.save(testFileDto);

        assertThat(result1.getName()).isNotEqualTo(result2.getName());
        assertThat(result1.getPath()).isNotEqualTo(result2.getPath());
        verify(fileRepository, times(2)).save(any(File.class));
    }

    @Test
    /**
 * Тестирует сценарий: поведение с пустыми данными при save_ when empty content_ should complete without errors
 * 
 * @see #save_WhenEmptyContent_ShouldCompleteWithoutErrors()
 */
    void save_WhenEmptyContent_ShouldCompleteWithoutErrors() {
        FileDto emptyFileDto = new FileDto("empty.txt", new byte[0]);
        when(fileRepository.save(any(File.class))).thenReturn(savedFile);

        File result = fileService.save(emptyFileDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);

        verify(fileRepository, times(1)).save(any(File.class));
    }

    @Test
    /**
 * Тестирует сценарий: обработку ошибки при get file by id_ with invalid ids_ should return empty or throw
 * 
 * @see #getFileById_WithInvalidIds_ShouldReturnEmptyOrThrow()
 */
    void getFileById_WithInvalidIds_ShouldReturnEmptyOrThrow() {
        when(fileRepository.findById(-1)).thenReturn(Optional.empty());
        when(fileRepository.findById(0)).thenReturn(Optional.empty());

        assertThat(fileService.getFileById(-1)).isEmpty();
        assertThat(fileService.getFileById(0)).isEmpty();

        verify(fileRepository, times(1)).findById(-1);
        verify(fileRepository, times(1)).findById(0);
    }
}