package ru.job4j.cars.service.participates;

import ru.job4j.cars.model.Participates;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.model.User;

import java.util.Optional;

public interface ParticipatesService {
    Optional<Participates> save(Post post, User user);
}
