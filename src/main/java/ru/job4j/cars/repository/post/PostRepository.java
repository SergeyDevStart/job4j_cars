package ru.job4j.cars.repository.post;

import ru.job4j.cars.dto.SearchDto;
import ru.job4j.cars.model.Post;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PostRepository {
    Optional<Post> create(Post post);

    boolean update(Post post);

    boolean delete(Post post);

    Optional<Post> findById(Integer id);

    Collection<Post> findAll();

    Collection<Post> findPostsWithFile();

    Collection<Post> findAllLastDay();

    Collection<Post> findByBrand(String brand);

    List<Post> findSearchResult(SearchDto searchDto);
}
