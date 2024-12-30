package ru.job4j.cars.service.post;

import org.springframework.web.multipart.MultipartFile;
import ru.job4j.cars.dto.PostCardDto;
import ru.job4j.cars.dto.PostCreateDto;
import ru.job4j.cars.dto.SearchDto;
import ru.job4j.cars.model.Post;

import java.util.*;

public interface PostService {
    boolean updateFiles(Integer postId, MultipartFile[] files);

    Optional<Post> create(PostCreateDto postDto, MultipartFile[] files);

    boolean update(Post post);

    boolean delete(Post post);

    Optional<Post> findById(Integer id);

    Collection<Post> findAll();

    Collection<Post> findPostsWithFile();

    Collection<Post> findAllLastDay();

    Collection<Post> findByBrand(String brand);

    Collection<PostCardDto> getPostCardDtoList(Collection<Post> posts);

    Map<String, List<String>> getCategories();

    List<Post> findSearchResult(SearchDto searchDto);
}
