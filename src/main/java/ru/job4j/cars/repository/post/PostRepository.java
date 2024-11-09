package ru.job4j.cars.repository.post;

import ru.job4j.cars.model.Post;

import java.util.Optional;

public interface PostRepository {
    Optional<Post> create(Post post);

    boolean update(Post post);

    boolean deleteById(Integer id);

    Optional<Post> findById(Integer id);
}
