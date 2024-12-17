package ru.job4j.cars.service.post;

import ru.job4j.cars.dto.FileDto;
import ru.job4j.cars.dto.PostCardDto;
import ru.job4j.cars.dto.PostCreateDto;
import ru.job4j.cars.model.File;
import ru.job4j.cars.model.Post;

import java.util.*;

public interface PostService {
    Optional<Post> create(PostCreateDto postDto, Set<FileDto> fileDtoSet);

    List<File> getSortedFiles(Set<File> files);

    boolean update(Post post);

    boolean delete(Post post);

    Optional<Post> findById(Integer id);

    Collection<Post> findAll();

    Collection<Post> findPostsWithFile();

    Collection<Post> findAllLastDay();

    Collection<Post> findByBrand(String brand);

    Collection<PostCardDto> getPostCardDtoList(Collection<Post> posts);

    Map<String, List<String>> getValuesForCreate();
}
