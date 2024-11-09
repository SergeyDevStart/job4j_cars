package ru.job4j.cars.repository.user;

import ru.job4j.cars.model.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> save(User user);

    Optional<User> findByLoginAndPassword(String login, String password);

    Optional<User> findById(Integer id);
}
