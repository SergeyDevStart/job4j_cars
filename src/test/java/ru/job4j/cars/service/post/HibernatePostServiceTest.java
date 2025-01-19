package ru.job4j.cars.service.post;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.cars.dto.FileDto;
import ru.job4j.cars.dto.HistoryOwnersDto;
import ru.job4j.cars.dto.PostCreateDto;
import ru.job4j.cars.mappers.PostMapper;
import ru.job4j.cars.model.*;
import ru.job4j.cars.repository.engine.EngineRepository;
import ru.job4j.cars.repository.post.PostRepository;
import ru.job4j.cars.service.file.FileService;
import ru.job4j.cars.service.historyowners.HibernateHistoryOwnersService;
import ru.job4j.cars.service.owner.OwnerService;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HibernatePostServiceTest {
    @Mock
    private PostRepository postRepository;
    @Mock
    private EngineRepository engineRepository;
    @Mock
    private FileService fileService;
    @Mock
    private OwnerService ownerService;
    @Mock
    private HibernateHistoryOwnersService historyOwnersService;
    @Mock
    private PostMapper postMapper;

    @InjectMocks
    private HibernatePostService postService;

    private Post post;
    private Car car;
    private File file;
    private Engine engine;
    private PostCreateDto postCreateDto;

    @BeforeEach
    void setUp() {
        engine = new Engine();
        engine.setId(1);

        car = new Car();
        car.setEngine(engine);

        post = new Post();
        post.setId(1);
        post.setDescription("Test Post");
        post.setCar(car);

        file = new File("file", "path");

        postCreateDto = new PostCreateDto();
        postCreateDto.setEngineId(1);
        postCreateDto.setDescription("Test Post");
    }

    @Test
    void whenCreatePostThenReturnOptionalPost() {
        MultipartFile[] files = {};

        when(postMapper.toPostFromPostCreateDto(postCreateDto)).thenReturn(post);
        when(engineRepository.findById(postCreateDto.getEngineId())).thenReturn(Optional.of(engine));
        when(postRepository.create(any(Post.class))).thenReturn(Optional.of(post));

        Optional<Post> result = postService.create(postCreateDto, files);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(post);
    }

    @Test
    void whenUpdateFilesThenFilesUpdated() {
        MultipartFile[] files = {};
        List<File> filesToDelete = List.of(file);
        Set<FileDto> filesDto = Set.of(new FileDto("file2", "content".getBytes()));

        when(fileService.findAllByPostId(post.getId())).thenReturn(filesToDelete);
        when(fileService.processFiles(files)).thenReturn(filesDto);
        when(postRepository.update(post)).thenReturn(true);
        when(fileService.toFileFromFileDto(any(FileDto.class))).thenReturn(new File("file", "path"));

        boolean result = postService.updateFiles(post, files);

        assertThat(result).isTrue();
        verify(fileService).deleteFiles(filesToDelete);
        verify(fileService).processFiles(files);
        verify(postRepository).update(post);
    }

    @Test
    void whenPriceHistoriesIsEmptyThenReturnEmptyList() {
        Set<PriceHistory> emptySet = new HashSet<>();

        List<PriceHistory> result = postService.getSortedPriceHistories(emptySet);

        assertThat(result).isEmpty();
    }

    @Test
    void whenPriceHistoriesHasValuesThenReturnSortedList() {
        PriceHistory first = new PriceHistory();
        first.setCreated(LocalDateTime.of(2023, 1, 1, 12, 0));
        PriceHistory second = new PriceHistory();
        second.setCreated(LocalDateTime.of(2022, 1, 1, 12, 0));
        PriceHistory third = new PriceHistory();
        third.setCreated(LocalDateTime.of(2024, 1, 1, 12, 0));
        Set<PriceHistory> priceHistories = Set.of(first, second, third);

        List<PriceHistory> result = postService.getSortedPriceHistories(priceHistories);

        assertThat(result).containsExactly(second, first, third);
        assertThat(result).isSortedAccordingTo(Comparator.comparing(PriceHistory::getCreated));
    }

    @Test
    void whenUpdatePriceHistoryThenPriceIsUpdated() {
        Set<PriceHistory> priceHistories = new HashSet<>();
        PriceHistory oldPrice = new PriceHistory();
        oldPrice.setAfter(100L);
        oldPrice.setCreated(LocalDateTime.now());
        priceHistories.add(oldPrice);
        post.setPriceHistories(priceHistories);

        when(postRepository.update(post)).thenReturn(true);

        boolean result = postService.updatePriceHistory(post, 200L);

        assertThat(result).isTrue();
        assertThat(post.getPriceHistories()).hasSize(2);
        verify(postRepository).update(post);
    }

    @Test
    void whenDeletePostWithFilesThenFilesAreDeleted() {
        List<File> filesToDelete = List.of(file);
        post.setFiles(new HashSet<>(filesToDelete));

        when(postRepository.delete(post)).thenReturn(true);

        boolean result = postService.delete(post);

        assertThat(result).isTrue();
        verify(fileService).deleteFiles(filesToDelete);
        verify(postRepository).delete(post);
    }

    @Test
    void whenFindByIdThenReturnPost() {
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));

        Optional<Post> result = postService.findById(post.getId());

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(post);
        verify(postRepository).findById(post.getId());
    }

    @Test
    void whenUpdateDescriptionThenDescriptionIsUpdated() {
        String newDescription = "Updated Description";

        when(postRepository.update(post)).thenReturn(true);

        boolean result = postService.updateDescription(post, newDescription);

        assertThat(result).isTrue();
        assertThat(post.getDescription()).isEqualTo(newDescription);
        verify(postRepository).update(post);
    }

    @Test
    void whenUpdateStatusThenStatusIsUpdated() {
        when(postRepository.update(post)).thenReturn(true);

        boolean result = postService.updateStatus(post);

        assertThat(result).isTrue();
        assertThat(post.isStatus()).isTrue();
    }

    @Test
    void whenUpdateHistoryOwnersThenIsUpdated() {
        HistoryOwnersDto historyOwnersDto = mock(HistoryOwnersDto.class);
        Owner owner = new Owner();
        owner.setName("Owner");

        when(historyOwnersDto.getOwnerName()).thenReturn("Owner");
        when(historyOwnersDto.getHistoryStartAt()).thenReturn(Date.from(Instant.now()));
        when(historyOwnersDto.getHistoryEndAt()).thenReturn(Date.from(Instant.now().plusSeconds(600)));
        when(ownerService.save(owner)).thenReturn(Optional.of(owner));
        when(postRepository.update(post)).thenReturn(true);

        boolean result = postService.updateHistoryOwners(post, historyOwnersDto);

        assertThat(result).isTrue();
        verify(ownerService).save(any(Owner.class));
        verify(historyOwnersService).createHistoryOwners(
                eq(car),
                eq(owner),
                any(LocalDate.class),
                any(LocalDate.class)
        );
        verify(postRepository).update(post);
    }

    @Test
    void whenUpdateHistoryOwnersAndOwnerIsNullThenReturnsFalse() {
        HistoryOwnersDto historyOwnersDto = mock(HistoryOwnersDto.class);

        when(historyOwnersDto.getOwnerName()).thenReturn("Owner");
        when(ownerService.save(any(Owner.class))).thenReturn(Optional.empty());

        boolean result = postService.updateHistoryOwners(post, historyOwnersDto);

        assertThat(result).isFalse();
        verifyNoInteractions(historyOwnersService);
        verifyNoInteractions(postRepository);
    }
}