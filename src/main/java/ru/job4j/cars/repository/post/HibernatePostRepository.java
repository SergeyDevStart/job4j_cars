package ru.job4j.cars.repository.post;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Brand;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.repository.CrudRepository;

import java.time.LocalDateTime;
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
    public boolean delete(Post post) {
        try {
            crudRepository.run(session -> session.delete(post));
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
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
    public Optional<Post> findById(Integer id) {
        return crudRepository.optional(
                "FROM Post WHERE id = :id",
                Post.class,
                Map.of("id", id)
        );
    }

    @Override
    public Collection<Post> findAll() {
        return crudRepository.query(
                "FROM Post",
                Post.class
        );
    }

    @Override
    public Collection<Post> findPostsWithFile() {
        return crudRepository.query(
                "FROM Post post WHERE post.files IS NOT EMPTY",
                Post.class
        );
    }

    @Override
    public Collection<Post> findAllLastDay() {
        return crudRepository.query(
                "FROM Post post WHERE post.created > :yesterday",
                Post.class,
                Map.of("yesterday", LocalDateTime.now().minusDays(1))
        );
    }

    @Override
    public Collection<Post> findByBrand(String brand) {
        try {
            Brand enumBrand = Brand.valueOf(brand.toUpperCase());
            return crudRepository.query(
                    "FROM Post p WHERE p.brand = :brand",
                    Post.class,
                    Map.of("brand", enumBrand)
            );
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid brand: " + brand, e);
        }
    }
}
