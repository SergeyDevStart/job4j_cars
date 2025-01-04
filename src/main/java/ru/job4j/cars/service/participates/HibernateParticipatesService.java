package ru.job4j.cars.service.participates;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.model.Participates;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.model.User;
import ru.job4j.cars.repository.participates.ParticipatesRepository;

import java.util.Optional;

@Service
@AllArgsConstructor
public class HibernateParticipatesService implements ParticipatesService {
    private final ParticipatesRepository participatesRepository;

    @Override
    public Optional<Participates> save(Post post, User user) {
        Participates participates = new Participates();
        participates.setUser(user);
        participates.setPost(post);
        return participatesRepository.save(participates);
    }
}
