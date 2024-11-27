package ru.job4j.cars.service.owner;

import ru.job4j.cars.model.Owner;

import java.util.Collection;
import java.util.Optional;

public interface OwnerService {
    Optional<Owner> save(Owner owner);

    Optional<Owner> findById(Integer id);

    boolean update(Owner owner);

    boolean deleteById(Integer id);

    Collection<Owner> findAll();
}
