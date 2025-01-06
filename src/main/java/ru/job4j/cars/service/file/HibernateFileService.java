package ru.job4j.cars.service.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.cars.dto.FileDto;
import ru.job4j.cars.model.File;
import ru.job4j.cars.repository.file.FileRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class HibernateFileService implements FileService {
    private final FileRepository fileRepository;
    @Value("${file.directory}")
    private String storageDirectory;

    @Override
    public Optional<FileDto> findById(Integer id) {
        var fileOptional = fileRepository.findById(id);
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
        return fileRepository.save(toFileFromFileDto(fileDto));
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
    public void delete(File file) {
        fileRepository.delete(file);
    }

    private void deleteFileByPath(String path) {
        try {
            Files.deleteIfExists(Path.of(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteFiles(List<File> filesToDelete) {
        for (File file : filesToDelete) {
            deleteFileByPath(file.getPath());
        }
    }

    @Override
    public Set<FileDto> processFiles(MultipartFile[] files) {
        Set<FileDto> filesDto = new HashSet<>();
        for (MultipartFile file : files) {
            try {
                var fileDto = getNewFileDto(file.getOriginalFilename(), file.getBytes());
                filesDto.add(fileDto);
            } catch (IOException e) {
                log.warn("Failed to process file: {}", file.getOriginalFilename(), e);
            }
        }
        return filesDto;
    }

    @Override
    public List<File> findAllByPostId(Integer postId) {
        return  fileRepository.findAllByPostId(postId);
    }

    @Override
    public FileDto getNewFileDto(String name, byte[] content) {
        return new FileDto(name, content);
    }
}
