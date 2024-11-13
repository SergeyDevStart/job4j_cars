package ru.job4j.cars.repository.file;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.File;
import ru.job4j.cars.repository.CrudRepository;

import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class HibernateFileRepository implements FileRepository {
    private final CrudRepository crudRepository;

    @Override
    public Optional<File> findById(Integer id) {
        return crudRepository.optional(
                "FROM File WHERE id = :id",
                File.class,
                Map.of("id", id)
        );
    }
}
