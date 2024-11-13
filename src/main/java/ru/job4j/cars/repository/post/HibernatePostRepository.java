package ru.job4j.cars.repository.post;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.repository.CrudRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@Repository
@Slf4j
public class HibernatePostRepository implements PostRepository {
    private final CrudRepository crudRepository;

    @Override
    public Optional<Post> create(Post post) {
        try {
            crudRepository.run(session -> session.persist(post));
            return Optional.of(post);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public boolean update(Post post) {
        try {
            crudRepository.run(session -> session.merge(post));
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean deleteById(Integer id) {
        return crudRepository.executeUpdate(
                "DELETE Post WHERE id = :id",
                Map.of("id", id)
        );
    }

    @Override
    public Optional<Post> findById(Integer id) {
        return crudRepository.optional(
                "FROM Post WHERE id = :id",
                Post.class,
                Map.of("id", id)
        );
    }

    @Override
    public Collection<Post> findPostsWithFile() {
        return crudRepository.query(
                "FROM Post p WHERE p.file_id IS NOT NULL",
                Post.class
        );
    }

    @Override
    public Collection<Post> findAllLastDay() {
        return crudRepository.query(
                "FROM Post p WHERE p.created = :today",
                Post.class,
                Map.of("today", LocalDate.now().atStartOfDay())
        );
    }

    @Override
    public Collection<Post> findByBrand(String brand) {
        return crudRepository.query(
                "FROM Post p WHERE p.brand = :brand",
                Post.class,
                Map.of("brand", brand)
        );
    }
}
