package ru.job4j.cars.service.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.model.User;
import ru.job4j.cars.repository.user.UserRepository;

import java.util.Optional;

@Service
@AllArgsConstructor
public class HibernateUserService implements UserService {
    private final UserRepository hibernateUserRepository;

    @Override
    public Optional<User> save(User user) {
        return hibernateUserRepository.save(user);
    }

    @Override
    public Optional<User> findByLoginAndPassword(String login, String password) {
        return hibernateUserRepository.findByLoginAndPassword(login, password);
    }

    @Override
    public Optional<User> findById(Integer id) {
        return hibernateUserRepository.findById(id);
    }
}
