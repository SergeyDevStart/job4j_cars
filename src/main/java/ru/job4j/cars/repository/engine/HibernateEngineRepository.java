package ru.job4j.cars.repository.engine;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Engine;
import ru.job4j.cars.repository.CrudRepository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
@Slf4j
public class HibernateEngineRepository implements EngineRepository {
    private final CrudRepository crudRepository;

    @Override
    public Optional<Engine> save(Engine engine) {
        try {
            crudRepository.run(session -> session.persist(engine));
            return Optional.of(engine);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Engine> findById(Integer id) {
        return crudRepository.optional(
                "FROM Engine WHERE id = :id",
                Engine.class,
                Map.of("id", id)
        );
    }

    @Override
    public boolean update(Engine engine) {
        try {
            crudRepository.run(session -> session.merge(engine));
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean deleteById(Integer id) {
        return crudRepository.executeUpdate(
                "DELETE Engine WHERE id = :id",
                Map.of("id", id)
        );
    }

    @Override
    public Collection<Engine> findAll() {
        return crudRepository.query(
                "FROM Engine",
                Engine.class
        );
    }
}
