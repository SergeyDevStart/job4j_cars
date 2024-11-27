package ru.job4j.cars.service.file;

import ru.job4j.cars.model.File;

import java.util.Optional;

public interface FileService {
    Optional<File> findById(Integer id);
}
