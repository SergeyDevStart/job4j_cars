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
import ru.job4j.cars.model.*;
import ru.job4j.cars.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

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

    private Set<File> getFiles() {
        File file1 = new File("file1", "path1");
        File file2 = new File("file2", "path2");
        return Set.of(file1, file2);
    }

    private Post getPost() {
        return Post.builder()
                .description("Desc")
                .created(LocalDateTime.now())
                .user(getUser())
                .priceHistories(new ArrayList<>())
                .files(getFiles())
                .brand(Brand.AUDI)
                .car(new Car())
                .participates(new HashSet<>())
                .build();
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

        assertThat(postRepository.findPostsWithFile()).contains(postWithFiles);
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
}