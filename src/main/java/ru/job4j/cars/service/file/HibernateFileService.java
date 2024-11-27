package ru.job4j.cars.service.file;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.model.File;
import ru.job4j.cars.repository.file.FileRepository;

import java.util.Optional;

@Service
@AllArgsConstructor
public class HibernateFileService implements FileService {
    private final FileRepository hibernateFileRepository;

    @Override
    public Optional<File> findById(Integer id) {
        return hibernateFileRepository.findById(id);
    }
}
