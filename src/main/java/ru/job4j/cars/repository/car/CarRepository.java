package ru.job4j.cars.repository.car;

import ru.job4j.cars.model.Car;

import java.util.Collection;
import java.util.Optional;

public interface CarRepository {
    Optional<Car> save(Car car);

    Optional<Car> findById(Integer id);

    boolean update(Car car);

    boolean deleteById(Integer id);

    Collection<Car> findAll();
}
