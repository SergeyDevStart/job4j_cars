package ru.job4j.cars.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.cars.dto.PostCardDto;
import ru.job4j.cars.dto.PostCreateDto;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.model.PriceHistory;
import ru.job4j.cars.model.User;
import ru.job4j.cars.service.post.PostService;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {
    @Mock
    private PostService postService;
    @InjectMocks
    private PostController postController;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(postController).build();
    }

    @Test
    void whenGetAllThenReturnPostListView() throws Exception {
        List<PostCardDto> posts = List.of(new PostCardDto());
        when(postService.getPostCardDtoList(any())).thenReturn(posts);

        mockMvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(view().name("posts/list"))
                .andExpect(model().attributeExists("posts"))
                .andExpect(model().attribute("posts", posts));
    }

    @Test
    void whenGetMyPostsThenReturnPostListView() throws Exception {
        User user = new User();
        user.setId(1);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", user);

        List<PostCardDto> posts = List.of(new PostCardDto());
        when(postService.getPostCardDtoList(any())).thenReturn(posts);

        mockMvc.perform(get("/posts/myPosts").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("posts/list"))
                .andExpect(model().attributeExists("posts"))
                .andExpect(model().attribute("posts", posts));
    }

    @Test
    void whenGetPostByIdThenReturnPostDetailView() throws Exception {
        Post post = new Post();
        post.setId(1);
        post.setDescription("Test Post");
        post.setPriceHistories(Set.of(new PriceHistory()));
        User user = new User();
        user.setId(1);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", user);

        when(postService.findById(1)).thenReturn(Optional.of(post));
        when(postService.getUserIdByPostId(1)).thenReturn(1);
        when(postService.getSortedPriceHistories(any())).thenReturn(List.of(new PriceHistory()));

        mockMvc.perform(get("/posts/1").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("posts/detail"))
                .andExpect(model().attributeExists("post", "price", "currentUser", "userIdByPost"));
    }

    @Test
    void whenUpdatePostThenReturnUpdateView() throws Exception {
        Post post = new Post();
        post.setId(1);
        post.setDescription("Test Post");

        when(postService.findById(1)).thenReturn(Optional.of(post));

        mockMvc.perform(get("/posts/update/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("posts/update"))
                .andExpect(model().attributeExists("post"))
                .andExpect(model().attribute("post", post));
    }

    @Test
    void whenSaveNewPostThenRedirectToMyPosts() throws Exception {
        User user = new User();
        user.setId(1);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", user);
        MockMultipartFile file = new MockMultipartFile(
                "files",
                "test.txt",
                "text/plain",
                "Test file content".getBytes()
        );

        when(postService.create(any(PostCreateDto.class), any(MultipartFile[].class))).thenReturn(Optional.of(new Post()));

        mockMvc.perform(multipart("/posts/create")
                        .file(file)
                        .session(session)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(redirectedUrl("/posts/myPosts"));
    }
}