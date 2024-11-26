package ru.job4j.cars.repository.car;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.Car;
import ru.job4j.cars.model.Engine;
import ru.job4j.cars.model.HistoryOwners;
import ru.job4j.cars.model.Owner;
import ru.job4j.cars.repository.CrudRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class HibernateCarRepositoryTest {
    private static SessionFactory sf;
    private static CarRepository carRepository;
    private static Set<HistoryOwners> historyOwners = new HashSet<>();

    @BeforeAll
    static void init() {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure().build();
        sf = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        carRepository = new HibernateCarRepository(new CrudRepository(sf));
    }

    @AfterAll
    static void sfClose() {
        if (sf != null) {
            sf.close();
        }
    }

    @AfterEach
    void clearTable() {
        Transaction transaction = null;
        try (var session = sf.openSession()) {
            transaction = session.beginTransaction();
            session.createQuery("DELETE FROM Car").executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }

    private Car getCar() {
        Car car = new Car();
        car.setOwner(new Owner());
        car.setEngine(new Engine());
        car.setHistoryOwners(historyOwners);
        return car;
    }

    @Test
    void whenSaveCarThenHasSame() {
        Car car = getCar();
        carRepository.save(car);
        assertThat(carRepository.findById(car.getId()).get()).isEqualTo(car);
    }

    @Test
    void whenCarWasUpdateSuccessfully() {
        var car = getCar();
        carRepository.save(car);
        car.getOwner().setName("ownerName");
        carRepository.update(car);
        var result = carRepository.findById(car.getId()).get();
        assertThat(result).isEqualTo(car);
        assertThat(result.getOwner().getName()).isEqualTo(car.getOwner().getName());
    }

    @Test
    void whenCarWasDeleteSuccessfully() {
        Car car = getCar();
        carRepository.save(car);
        assertThat(carRepository.deleteById(car.getId())).isTrue();
        assertThat(carRepository.findById(car.getId())).isEmpty();
    }

    @Test
    void whenFindAllThenHasSame() {
        Car car1 = getCar();
        Car car2 = getCar();
        carRepository.save(car1);
        carRepository.save(car2);
        List<Car> expected = List.of(car1, car2);
        assertThat(carRepository.findAll()).isEqualTo(expected);
    }
}