package ru.job4j.cars.repository.file;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.File;
import ru.job4j.cars.repository.CrudRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@Slf4j
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

    @Override
    public Optional<File> save(File file) {
        try {
            crudRepository.run(session -> session.persist(file));
            return Optional.of(file);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<File> findAllByPostId(Integer postId) {
        return crudRepository.query(
                "FROM File f WHERE f.post.id = :postId",
                File.class,
                Map.of("postId", postId)
        );
    }

    @Override
    public void delete(File file) {
        try {
            crudRepository.run(session -> session.delete(file));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
