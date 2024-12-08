package ru.job4j.cars.service.post;

import ru.job4j.cars.dto.PostCardDto;
import ru.job4j.cars.dto.PostCreateDto;
import ru.job4j.cars.model.Post;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PostService {
    Optional<Post> create(Post post);

    Optional<Post> create(PostCreateDto dto);

    boolean update(Post post);

    boolean delete(Post post);

    Optional<Post> findById(Integer id);

    Collection<Post> findAll();

    Collection<Post> findPostsWithFile();

    Collection<Post> findAllLastDay();

    Collection<Post> findByBrand(String brand);

    Collection<PostCardDto> getPostCardDtoList(Collection<Post> posts);
}
