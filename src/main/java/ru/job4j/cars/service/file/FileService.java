package ru.job4j.cars.service.file;

import org.springframework.web.multipart.MultipartFile;
import ru.job4j.cars.dto.FileDto;
import ru.job4j.cars.model.File;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FileService {
    Set<FileDto> processFiles(MultipartFile[] files);

    Optional<FileDto> findById(Integer id);

    Optional<File> save(FileDto fileDto);

    File toFileFromFileDto(FileDto fileDto);

    void delete(File file);

    FileDto getNewFileDto(String name, byte[] content);

    void deleteFiles(List<File> filesToDelete);

    List<File> findAllByPostId(Integer postId);
}
