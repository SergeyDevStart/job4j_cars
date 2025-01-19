package ru.job4j.cars.repository.post;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.dto.SearchDto;
import ru.job4j.cars.model.*;
import ru.job4j.cars.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class HibernatePostRepositoryTest {
    private static SessionFactory sf;
    private static CrudRepository crudRepository;
    private static PostRepository postRepository;

    @BeforeAll
    static void init() {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure().build();
        sf = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        crudRepository = new CrudRepository(sf);
        postRepository = new HibernatePostRepository(crudRepository);
    }

    @AfterAll
    static void sfClose() {
        if (sf != null) {
            sf.close();
        }
    }

    @AfterEach
    void clearTable() {
        Transaction transaction = null;
        try (var session = sf.openSession()) {
            transaction = session.beginTransaction();
            session.createQuery("DELETE FROM File").executeUpdate();
            session.createQuery("DELETE FROM Participates").executeUpdate();
            session.createQuery("DELETE FROM Post").executeUpdate();
            session.createQuery("DELETE FROM Car").executeUpdate();
            session.createQuery("DELETE FROM User").executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }

    private User getUser() {
        User user = new User();
        user.setLogin("login");
        user.setPassword("password");
        crudRepository.run(session -> session.save(user));
        return user;
    }

    private Car getCar() {
        Car car = Car.builder().name("AUDI").build();
        crudRepository.run(session -> session.persist(car));
        return car;
    }

    private void installFiles(Post post) {
        File file1 = new File("file1", "path1");
        File file2 = new File("file2", "path2");
        post.addFile(file1);
        post.addFile(file2);
    }

    private Post getPost() {
        Post post = new Post();
        post.setDescription("Desc");
        post.setCreated(LocalDateTime.now());
        post.setUser(getUser());
        post.setBrand(Brand.AUDI);
        post.setStatus(true);
        post.setCar(getCar());
        installFiles(post);
        return post;
    }

    @Test
    void whenCreatePostThenHasSame() {
        Post post = getPost();
        postRepository.create(post);
        assertThat(postRepository.findById(post.getId()).get()).isEqualTo(post);
    }

    @Test
    void whenPostWasUpdateSuccessfully() {
        Post post = getPost();
        postRepository.create(post);

        post.setDescription("UpdatedDescription");
        postRepository.update(post);
        Post result = postRepository.findById(post.getId()).get();
        assertThat(result).isEqualTo(post);
        assertThat(result.getDescription()).isEqualTo(post.getDescription());
    }

    @Test
    void whenPostWasDeleteSuccessfully() {
        Post post = getPost();
        postRepository.create(post);
        assertThat(postRepository.delete(post)).isTrue();
        assertThat(postRepository.findById(post.getId())).isEmpty();
    }

    @Test
    void whenFindFileWithFileThenGetPostWithFile() {
        Post postWithFiles = getPost();
        Post postWithoutFiles = getPost();
        postWithoutFiles.setFiles(new HashSet<>());

        postRepository.create(postWithFiles);
        postRepository.create(postWithoutFiles);
        var expected = postRepository.findPostsWithFile();

        assertThat(expected).contains(postWithFiles);
    }

    @Test
    void whenFindAllThenGetSame() {
        Post post1 = getPost();
        Post post2 = getPost();
        post2.setDescription("Other Post");

        postRepository.create(post1);
        postRepository.create(post2);

        Collection<Post> expected = List.of(post1, post2);
        assertThat(postRepository.findAll()).isEqualTo(expected);
    }

    @Test
    void whenFindAllLastDayThenGetPostWithLastDay() {
        Post post1 = getPost();
        Post post2 = getPost();
        post2.setDescription("Other Post");
        post2.setCreated(LocalDateTime.now().minusDays(2));

        postRepository.create(post1);
        postRepository.create(post2);

        Collection<Post> expected = List.of(post1);

        assertThat(postRepository.findAllLastDay()).isEqualTo(expected);
    }

    @Test
    void whenFindByBrandThenGetPostsWithThisBrand() {
        Post post1 = getPost();
        Post post2 = getPost();
        Post postWithOtherBrand = getPost();
        postWithOtherBrand.setBrand(Brand.BMW);

        postRepository.create(post1);
        postRepository.create(post2);
        postRepository.create(postWithOtherBrand);

        Collection<Post> expected = List.of(post1, post2);

        assertThat(postRepository.findByBrand("AUDI")).isEqualTo(expected);
    }

    @Test
    void whenCreatePostThrowsExceptionThenReturnEmptyOptional() {
        Post post = getPost();
        CrudRepository mockedCrudRepository = mock(CrudRepository.class);
        PostRepository postRepository = new HibernatePostRepository(mockedCrudRepository);

        doThrow(new RuntimeException("Database error"))
                .when(mockedCrudRepository).run(any());

        Optional<Post> result = postRepository.create(post);

        assertThat(result).isEmpty();
        verify(mockedCrudRepository).run(any());
    }

    @Test
    void whenUpdatePostThrowsExceptionThenReturnFalse() {
        Post post = getPost();
        CrudRepository mockedCrudRepository = mock(CrudRepository.class);
        PostRepository postRepository = new HibernatePostRepository(mockedCrudRepository);

        doThrow(new RuntimeException("Database error"))
                .when(mockedCrudRepository).run(any());

        boolean result = postRepository.update(post);

        assertThat(result).isFalse();
        verify(mockedCrudRepository).run(any());
    }

    @Test
    void whenDeletePostThrowsExceptionThenReturnFalse() {
        Post post = getPost();
        CrudRepository mockedCrudRepository = mock(CrudRepository.class);
        PostRepository postRepository = new HibernatePostRepository(mockedCrudRepository);

        doThrow(new RuntimeException("Database error"))
                .when(mockedCrudRepository).run(any());

        boolean result = postRepository.delete(post);

        assertThat(result).isFalse();
        verify(mockedCrudRepository).run(any());
    }

    @Test
    void whenFindAllByUserIdThenGetUserPosts() {
        User user = getUser();
        Post firstPost = getPost();
        Post secondPost = getPost();
        firstPost.setUser(user);
        secondPost.setUser(user);
        postRepository.create(firstPost);
        postRepository.create(secondPost);

        var result = postRepository.findAllByUserId(user.getId());
        assertThat(result).containsExactly(firstPost, secondPost);
    }

    @Test
    void whenFindAllPostsBySubscriptionsThenGetCorrectPosts() {
        User user = getUser();
        Post post = getPost();
        post.setUser(user);
        postRepository.create(post);

        Participates participates = new Participates();
        participates.setPost(post);
        participates.setUser(user);
        crudRepository.run(session -> session.save(participates));

        var result = postRepository.findAllPostsBySubscriptions(user.getId());
        assertThat(result).containsExactly(post);
    }

    @Test
    void whenFindSearchResultThenGetCorrectPosts() {
        SearchDto searchDto = new SearchDto();
        searchDto.setBrand("AUDI");

        Post post = getPost();
        post.setBrand(Brand.AUDI);

        CrudRepository mockedCrudRepository = mock(CrudRepository.class);
        PostRepository postRepository = new HibernatePostRepository(mockedCrudRepository);

        when(mockedCrudRepository.tx(any()))
                .thenReturn(List.of(post));

        List<Post> result = postRepository.findSearchResult(searchDto);

        assertThat(result).containsExactly(post);
        verify(mockedCrudRepository).tx(any());
    }

    @Test
    void whenFindSearchResultWithoutParametersThenGetAllPosts() {
        SearchDto searchDto = new SearchDto();

        Post postOne = getPost();
        postOne.setBrand(Brand.AUDI);
        Post postTwo = getPost();
        postTwo.setBrand(Brand.BMW);

        CrudRepository mockedCrudRepository = mock(CrudRepository.class);
        PostRepository postRepository = new HibernatePostRepository(mockedCrudRepository);

        when(mockedCrudRepository.tx(any()))
                .thenReturn(List.of(postOne, postTwo));

        List<Post> result = postRepository.findSearchResult(searchDto);

        assertThat(result).containsExactly(postOne, postTwo);
        verify(mockedCrudRepository).tx(any());
    }
}