package ru.job4j.cars.repository.participates;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Participates;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.repository.CrudRepository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Repository
@Slf4j
@AllArgsConstructor
public class HibernateParticipatesRepository implements ParticipatesRepository {
    private final CrudRepository crudRepository;

    @Override
    public Optional<Participates> save(Participates participates) {
        try {
            crudRepository.run(session -> session.persist(participates));
            return Optional.of(participates);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return Optional.empty();
    }
}
