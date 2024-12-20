package ru.job4j.cars.service.file;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.job4j.cars.dto.FileDto;
import ru.job4j.cars.model.File;
import ru.job4j.cars.repository.file.FileRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HibernateFileService implements FileService {
    private final FileRepository hibernateFileRepository;
    @Value("${file.directory}")
    private String storageDirectory;

    @Override
    public Optional<FileDto> findById(Integer id) {
        var fileOptional = hibernateFileRepository.findById(id);
        if (fileOptional.isEmpty()) {
            return Optional.empty();
        }
        var content = readFileAsBytes(fileOptional.get().getPath());
        return Optional.of(new FileDto(fileOptional.get().getName(), content));
    }

    private byte[] readFileAsBytes(String path) {
        try {
            return Files.readAllBytes(Path.of(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<File> save(FileDto fileDto) {
        return hibernateFileRepository.save(toFileFromFileDto(fileDto));
    }

    private String getNewFilePath(String sourceName) {
        return storageDirectory + java.io.File.separator + UUID.randomUUID() + sourceName;
    }

    @Override
    public File toFileFromFileDto(FileDto fileDto) {
        var path = getNewFilePath(fileDto.getName());
        writeFileBytes(path, fileDto.getContent());
        return new File(fileDto.getName(), path);
    }

    private void writeFileBytes(String path, byte[] content) {
        try {
            Files.write(Path.of(path), content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(Integer id) {
        var fileOptional = hibernateFileRepository.findById(id);
        if (fileOptional.isPresent()) {
            deleteFile(fileOptional.get().getPath());
            hibernateFileRepository.delete(fileOptional.get());
        }
    }

    private void deleteFile(String path) {
        try {
            Files.deleteIfExists(Path.of(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteFiles(List<File> filesToDelete) {
        for (File file : filesToDelete) {
            deleteFile(file.getPath());
        }
    }

    @Override
    public FileDto getNewFileDto(String name, byte[] content) {
        return new FileDto(name, content);
    }
}
