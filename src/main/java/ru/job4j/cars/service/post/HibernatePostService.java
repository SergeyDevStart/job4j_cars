package ru.job4j.cars.service.post;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.job4j.cars.dto.FileDto;
import ru.job4j.cars.dto.PostCardDto;
import ru.job4j.cars.dto.PostCreateDto;
import ru.job4j.cars.mappers.PostMapper;
import ru.job4j.cars.model.*;
import ru.job4j.cars.repository.engine.EngineRepository;
import ru.job4j.cars.repository.post.PostRepository;
import ru.job4j.cars.service.file.FileService;

import java.util.*;
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
    public Optional<Post> create(PostCreateDto postDto, Set<FileDto> filesDtoSet) {
        Post post = getPostFromPostDto(postDto);
        saveNewFile(post, filesDtoSet);
        return hibernatePostRepository.create(post);
    }

    private void saveNewFile(Post post, Set<FileDto> fileDtoSet) {
        for (FileDto fileDto : fileDtoSet) {
            File file = hibernateFileService.toFileFromFileDto(fileDto);
            post.addFile(file);
        }
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
        var optionalPost =  hibernatePostRepository.findById(id);
        if (optionalPost.isEmpty()) {
            log.warn("Post with ID {} not found", id);
            return Optional.empty();
        }
        return optionalPost;
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
                .map(post -> {
                    var dto = postMapper.toPostCardDtoFromPost(post);
                    dto.setId(post.getId());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, List<String>> getValuesForCreate() {
        Map<String, List<String>> enumMap = new HashMap<>();
        enumMap.put("body", Arrays.stream(Body.values()).map(Enum::name).toList());
        enumMap.put("brand", Arrays.stream(Brand.values()).map(Enum::name).toList());
        enumMap.put("gearbox", Arrays.stream(Gearbox.values()).map(Enum::name).toList());
        enumMap.put("typeDrive", Arrays.stream(TypeDrive.values()).map(Enum::name).toList());
        return enumMap;
    }

    @Override
    public List<File> getSortedFiles(Set<File> files) {
        List<File> sortedFiles = new ArrayList<>(files);
        sortedFiles.sort(Comparator.comparing(File::getId));
        return sortedFiles;
    }
}
