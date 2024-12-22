package ru.job4j.cars.service.file;

import ru.job4j.cars.dto.FileDto;
import ru.job4j.cars.model.File;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FileService {
    Optional<FileDto> findById(Integer id);

    Optional<File> save(FileDto fileDto);

    File toFileFromFileDto(FileDto fileDto);

    void deleteById(Integer id);

    FileDto getNewFileDto(String name, byte[] content);

    void deleteFiles(List<File> filesToDelete);

    List<File> findAllByPostId(Integer postId);
}
