package ru.job4j.cars.repository.historyowners;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.HistoryOwners;
import ru.job4j.cars.repository.CrudRepository;

import java.util.Optional;

@AllArgsConstructor
@Repository
@Slf4j
public class HibernateHistoryOwnerRepository implements HistoryOwnersRepository {
    private final CrudRepository crudRepository;

    @Override
    public Optional<HistoryOwners> save(HistoryOwners historyOwners) {
        try {
            crudRepository.run(session -> session.persist(historyOwners));
            return Optional.of(historyOwners);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return Optional.empty();
    }
}
