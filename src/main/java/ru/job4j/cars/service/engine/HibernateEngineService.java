package ru.job4j.cars.service.engine;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.model.Engine;
import ru.job4j.cars.repository.engine.EngineRepository;

import java.util.Collection;
import java.util.Optional;

@Service
@AllArgsConstructor
public class HibernateEngineService implements EngineService {
    private final EngineRepository hibernateEngineRepository;

    @Override
    public Optional<Engine> save(Engine engine) {
        return hibernateEngineRepository.save(engine);
    }

    @Override
    public Optional<Engine> findById(Integer id) {
        return hibernateEngineRepository.findById(id);
    }

    @Override
    public boolean update(Engine engine) {
        return hibernateEngineRepository.update(engine);
    }

    @Override
    public boolean deleteById(Integer id) {
        return hibernateEngineRepository.deleteById(id);
    }

    @Override
    public Collection<Engine> findAll() {
        return hibernateEngineRepository.findAll();
    }
}
