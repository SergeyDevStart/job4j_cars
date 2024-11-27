package ru.job4j.cars.service.user;

import ru.job4j.cars.model.User;

import java.util.Optional;

public interface UserService {
    Optional<User> save(User user);

    Optional<User> findByLoginAndPassword(String login, String password);

    Optional<User> findById(Integer id);
}
