package ru.job4j.cars.service.post;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.cars.dto.FileDto;
import ru.job4j.cars.dto.PostCardDto;
import ru.job4j.cars.dto.PostCreateDto;
import ru.job4j.cars.dto.SearchDto;
import ru.job4j.cars.mappers.PostMapper;
import ru.job4j.cars.model.*;
import ru.job4j.cars.repository.engine.EngineRepository;
import ru.job4j.cars.repository.historyowners.HistoryOwnersRepository;
import ru.job4j.cars.repository.post.PostRepository;
import ru.job4j.cars.service.file.FileService;
import ru.job4j.cars.service.owner.OwnerService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class HibernatePostService implements PostService {
    private final PostRepository postRepository;
    private final EngineRepository engineRepository;
    private final FileService fileService;
    private final OwnerService ownerService;
    private final HistoryOwnersRepository historyOwnersRepository;
    private final PostMapper postMapper;

    @Override
    public Optional<Post> create(PostCreateDto postDto, MultipartFile[] files) {
        Set<FileDto> filesDto = processFiles(files);
        Post post = getPostFromPostDto(postDto);
        saveNewFile(post, filesDto);
        saveHistoryOwners(post, postDto.getHistoryStartAt());
        return postRepository.create(post);
    }

    private Set<FileDto> processFiles(MultipartFile[] files) {
        Set<FileDto> filesDto = new HashSet<>();
        for (MultipartFile file : files) {
            try {
                var fileDto = fileService.getNewFileDto(file.getOriginalFilename(), file.getBytes());
                filesDto.add(fileDto);
            } catch (IOException e) {
                log.warn("Failed to process file: {}", file.getOriginalFilename(), e);
            }
        }
        return filesDto;
    }

    private void saveNewFile(Post post, Set<FileDto> fileDtoSet) {
        for (FileDto fileDto : fileDtoSet) {
            File file = fileService.toFileFromFileDto(fileDto);
            post.addFile(file);
        }
    }

    private void saveHistoryOwners(Post post, Date historyStartAt) {
        if (historyStartAt == null) {
            throw new IllegalArgumentException("History start date cannot be null");
        }
        Car car = post.getCar();
        Owner owner = getOrCreateOwner(post, car);
        var historyOwners = createHistoryOwners(car, owner, historyStartAt);
        historyOwnersRepository.save(historyOwners);
    }

    private Owner getOrCreateOwner(Post post, Car car) {
        var optionalOwner = ownerService.findByUserId(post.getUser().getId());
        Owner owner;
        if (optionalOwner.isEmpty()) {
            owner = car.getOwner();
            owner.setUser(post.getUser());
            ownerService.save(owner);
        } else {
            owner = optionalOwner.get();
            car.setOwner(owner);
        }
        return owner;
    }

    private HistoryOwners createHistoryOwners(Car car, Owner owner, Date historyStartAt) {
        HistoryOwners historyOwners = new HistoryOwners();
        historyOwners.setCar(car);
        historyOwners.setOwner(owner);
        historyOwners.setStartAt(historyStartAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        historyOwners.setEndAt(LocalDate.now(ZoneId.systemDefault()));
        return historyOwners;
    }

    @Override
    public boolean update(Post post) {
        return postRepository.update(post);
    }

    @Override
    public boolean delete(Post post) {
        return postRepository.delete(post);
    }

    @Override
    public Optional<Post> findById(Integer id) {
        var optionalPost =  postRepository.findById(id);
        if (optionalPost.isEmpty()) {
            log.warn("Post with ID {} not found", id);
            return Optional.empty();
        }
        return optionalPost;
    }

    @Override
    public Collection<Post> findAll() {
        return postRepository.findAll();
    }

    @Override
    public Collection<Post> findPostsWithFile() {
        return postRepository.findPostsWithFile();
    }

    @Override
    public Collection<Post> findAllLastDay() {
        return postRepository.findAllLastDay();
    }

    @Override
    public Collection<Post> findByBrand(String brand) {
        return postRepository.findByBrand(brand);
    }

    private <T> Post getPostFromPostDto(T dto) {
        if (dto instanceof PostCreateDto) {
            Post post = postMapper.toPostFromPostCreateDto((PostCreateDto) dto);
            Engine engine = engineRepository.findById(((PostCreateDto) dto).getEngineId()).orElseThrow(() ->
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

    @Override
    public Map<String, List<String>> getCategories() {
        Map<String, List<String>> enumMap = new HashMap<>();
        enumMap.put("body", Arrays.stream(Body.values()).map(Enum::name).toList());
        enumMap.put("brand", Arrays.stream(Brand.values()).map(Enum::name).toList());
        enumMap.put("gearbox", Arrays.stream(Gearbox.values()).map(Enum::name).toList());
        enumMap.put("typeDrive", Arrays.stream(TypeDrive.values()).map(Enum::name).toList());
        return enumMap;
    }

    @Override
    public List<Post> findSearchResult(SearchDto searchDto) {
        return postRepository.findSearchResult(searchDto);
    }
}
