package ru.job4j.cars.service.post;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.cars.dto.*;
import ru.job4j.cars.mappers.PostMapper;
import ru.job4j.cars.model.*;
import ru.job4j.cars.repository.engine.EngineRepository;
import ru.job4j.cars.repository.post.PostRepository;
import ru.job4j.cars.service.file.FileService;
import ru.job4j.cars.service.historyowners.HibernateHistoryOwnersService;
import ru.job4j.cars.service.owner.OwnerService;

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
    private final HibernateHistoryOwnersService historyOwnersService;
    private final PostMapper postMapper;

    @Override
    public Optional<Post> create(PostCreateDto postDto, MultipartFile[] files) {
        Post post = getPostFromPostDto(postDto);
        if (files.length != 0 && files[0].getSize() != 0) {
            saveNewFile(post, fileService.processFiles(files));
        }
        historyOwnersService.saveHistoryOwnersInPost(post, postDto.getHistoryStartAt());
        return postRepository.create(post);
    }

    private void saveNewFile(Post post, Set<FileDto> fileDtoSet) {
        post.getFiles().clear();
        for (FileDto fileDto : fileDtoSet) {
            File file = fileService.toFileFromFileDto(fileDto);
            post.addFile(file);
        }
    }

    @Override
    public boolean update(Post post) {
        return postRepository.update(post);
    }

    @Override
    public boolean updateFiles(Post post, MultipartFile[] files) {
        var filesToDelete = fileService.findAllByPostId(post.getId());
        fileService.deleteFiles(filesToDelete);
        saveNewFile(post, fileService.processFiles(files));
        return update(post);
    }

    @Override
    public boolean updateHistoryOwners(Post post, HistoryOwnersDto historyOwnersDto) {
        var optionalOwner = ownerService.save(Owner.builder().name(historyOwnersDto.getOwnerName()).build());
        if (optionalOwner.isEmpty()) {
            log.warn("Error creating entity Owner");
            return false;
        }
        Car car = post.getCar();
        var startAt = historyOwnersDto.getHistoryStartAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        var endAt = historyOwnersDto.getHistoryEndAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        historyOwnersService.createHistoryOwners(car, optionalOwner.get(), startAt, endAt);
        return update(post);
    }

    @Override
    public boolean updatePriceHistory(Post post, Long newPrice) {
        List<PriceHistory> sortedPriceHistories = getSortedPriceHistories(post.getPriceHistories());
        long oldPrice = sortedPriceHistories.get(sortedPriceHistories.size() - 1).getAfter();
        PriceHistory priceHistory = new PriceHistory();
        priceHistory.setBefore(oldPrice);
        priceHistory.setAfter(newPrice);
        post.getPriceHistories().add(priceHistory);
        return update(post);
    }

    @Override
    public boolean updateStatus(Post post) {
        post.setStatus(true);
        return update(post);
    }

    @Override
    public boolean updateDescription(Post post, String description) {
        post.setDescription(description);
        return update(post);
    }

    @Override
    public boolean delete(Post post) {
        var filesToDelete = new ArrayList<>(post.getFiles());
        if (!filesToDelete.isEmpty()) {
            fileService.deleteFiles(filesToDelete);
        }
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
    public Collection<Post> findAllByUserId(Integer userId) {
        return postRepository.findAllByUserId(userId);
    }

    @Override
    public Collection<Post> findAllPostsBySubscriptions(Integer userId) {
        return postRepository.findAllPostsBySubscriptions(userId);
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

    private Post getPostFromPostDto(PostCreateDto dto) {
        Post post = postMapper.toPostFromPostCreateDto(dto);
        Engine engine = engineRepository.findById(dto.getEngineId()).orElseThrow(() ->
                new IllegalArgumentException("Engine with id not found"));
        post.getCar().setEngine(engine);
        return post;
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
    public List<PriceHistory> getSortedPriceHistories(Set<PriceHistory> priceHistories) {
        return priceHistories.isEmpty() ? new ArrayList<>() : priceHistories.stream()
                .sorted(Comparator.comparing(PriceHistory::getCreated))
                .collect(Collectors.toList());
    }

    @Override
    public List<Post> findSearchResult(SearchDto searchDto) {
        return postRepository.findSearchResult(searchDto);
    }

    @Override
    public Integer getUserIdByPostId(Integer postId) {
        return postRepository.getUserIdByPostId(postId);
    }
}
