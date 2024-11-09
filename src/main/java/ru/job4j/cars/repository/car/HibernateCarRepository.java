package ru.job4j.cars.repository.car;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Car;
import ru.job4j.cars.repository.CrudRepository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@Repository
@Slf4j
public class HibernateCarRepository implements CarRepository {
    private final CrudRepository crudRepository;

    @Override
    public Optional<Car> save(Car car) {
        try {
            crudRepository.run(session -> session.persist(car));
            return Optional.of(car);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Car> findById(Integer id) {
        return crudRepository.optional(
                "FROM Car WHERE id = :id",
                Car.class,
                Map.of("id", id)
        );
    }

    @Override
    public boolean update(Car car) {
        try {
            crudRepository.run(session -> session.merge(car));
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean deleteById(Integer id) {
        return crudRepository.executeUpdate(
                "DELETE Car WHERE id = :id",
                Map.of("id", id)
        );
    }

    @Override
    public Collection<Car> findAll() {
        return crudRepository.query(
                "FROM Car",
                Car.class
        );
    }
}
