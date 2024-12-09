package ru.job4j.cars.service.post;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.job4j.cars.dto.FileDto;
import ru.job4j.cars.dto.PostCardDto;
import ru.job4j.cars.dto.PostCreateDto;
import ru.job4j.cars.mappers.PostMapper;
import ru.job4j.cars.model.Engine;
import ru.job4j.cars.model.File;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.repository.engine.EngineRepository;
import ru.job4j.cars.repository.post.PostRepository;
import ru.job4j.cars.service.file.FileService;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class HibernatePostService implements PostService {
    private final PostRepository hibernatePostRepository;
    private final EngineRepository hibernateEngineRepository;
    private final FileService hibernateFileService;
    private final PostMapper postMapper;

    @Override
    public Optional<Post> create(PostCreateDto postDto, FileDto fileDto) {
        Post post = getPostFromPostDto(postDto);
        saveNewFile(post, fileDto);
        return hibernatePostRepository.create(post);
    }

    private void saveNewFile(Post post, FileDto fileDto) {
        File file = hibernateFileService.toFileFromFileDto(fileDto);
        if (post.getFiles() == null) {
            post.setFiles(new HashSet<>());
        }
        post.getFiles().add(file);
    }

    @Override
    public boolean update(Post post) {
        return hibernatePostRepository.update(post);
    }

    @Override
    public boolean delete(Post post) {
        return hibernatePostRepository.delete(post);
    }

    @Override
    public Optional<Post> findById(Integer id) {
        return hibernatePostRepository.findById(id);
    }

    @Override
    public Collection<Post> findAll() {
        return hibernatePostRepository.findAll();
    }

    @Override
    public Collection<Post> findPostsWithFile() {
        return hibernatePostRepository.findPostsWithFile();
    }

    @Override
    public Collection<Post> findAllLastDay() {
        return hibernatePostRepository.findAllLastDay();
    }

    @Override
    public Collection<Post> findByBrand(String brand) {
        return hibernatePostRepository.findByBrand(brand);
    }

    private <T> Post getPostFromPostDto(T dto) {
        if (dto instanceof PostCreateDto) {
            Post post = postMapper.toPostFromPostCreateDto((PostCreateDto) dto);
            Engine engine = hibernateEngineRepository.findById(((PostCreateDto) dto).getEngineId()).orElseThrow(() ->
                    new IllegalArgumentException("Engine with id not found"));
            post.getCar().setEngine(engine);
            return post;
        } else {
            throw new IllegalArgumentException("Unsupported DTO type");
        }
    }

    @Override
    public List<PostCardDto> getPostCardDtoList(Collection<Post> posts) {
        return posts.stream()
                .map(postMapper::toPostCardDtoFromPost)
                .collect(Collectors.toList());
    }
}
