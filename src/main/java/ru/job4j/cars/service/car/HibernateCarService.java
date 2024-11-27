package ru.job4j.cars.service.car;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.model.Car;
import ru.job4j.cars.repository.car.CarRepository;

import java.util.Collection;
import java.util.Optional;

@Service
@AllArgsConstructor
public class HibernateCarService implements CarService {
    private final CarRepository hibernateCarRepository;

    @Override
    public Optional<Car> save(Car car) {
        return hibernateCarRepository.save(car);
    }

    @Override
    public Optional<Car> findById(Integer id) {
        return hibernateCarRepository.findById(id);
    }

    @Override
    public boolean update(Car car) {
        return hibernateCarRepository.update(car);
    }

    @Override
    public boolean deleteById(Integer id) {
        return hibernateCarRepository.deleteById(id);
    }

    @Override
    public Collection<Car> findAll() {
        return hibernateCarRepository.findAll();
    }
}
