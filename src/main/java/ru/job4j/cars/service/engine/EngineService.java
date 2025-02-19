package ru.job4j.cars.service.engine;

import ru.job4j.cars.model.Engine;

import java.util.Collection;
import java.util.Optional;

public interface EngineService {
    Optional<Engine> save(Engine engine);

    Optional<Engine> findById(Integer id);

    boolean update(Engine engine);

    boolean deleteById(Integer id);

    Collection<Engine> findAll();
}
