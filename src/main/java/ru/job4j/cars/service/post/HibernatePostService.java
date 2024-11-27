package ru.job4j.cars.service.post;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.repository.post.PostRepository;

import java.util.Collection;
import java.util.Optional;

@Service
@AllArgsConstructor
public class HibernatePostService implements PostService {
    private final PostRepository hibernatePostRepository;

    @Override
    public Optional<Post> create(Post post) {
        return hibernatePostRepository.create(post);
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
}
