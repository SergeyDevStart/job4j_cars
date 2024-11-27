package ru.job4j.cars.service.owner;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.model.Owner;
import ru.job4j.cars.repository.owner.OwnerRepository;

import java.util.Collection;
import java.util.Optional;

@Service
@AllArgsConstructor
public class HibernateOwnerService implements OwnerService {
    private final OwnerRepository hibernateOwnerRepository;

    @Override
    public Optional<Owner> save(Owner owner) {
        return hibernateOwnerRepository.save(owner);
    }

    @Override
    public Optional<Owner> findById(Integer id) {
        return hibernateOwnerRepository.findById(id);
    }

    @Override
    public boolean update(Owner owner) {
        return hibernateOwnerRepository.update(owner);
    }

    @Override
    public boolean deleteById(Integer id) {
        return hibernateOwnerRepository.deleteById(id);
    }

    @Override
    public Collection<Owner> findAll() {
        return hibernateOwnerRepository.findAll();
    }
}
