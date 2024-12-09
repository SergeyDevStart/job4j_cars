package ru.job4j.cars.repository.file;

import ru.job4j.cars.model.File;

import java.util.Collection;
import java.util.Optional;

public interface FileRepository {
    Optional<File> findById(Integer id);

    Optional<File> save(File file);

    Collection<File> findAll();

    void delete(File file);
}
