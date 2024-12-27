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
    private final OwnerRepository ownerRepository;

    @Override
    public Optional<Owner> save(Owner owner) {
        return ownerRepository.save(owner);
    }

    @Override
    public Optional<Owner> findById(Integer id) {
        return ownerRepository.findById(id);
    }

    @Override
    public boolean update(Owner owner) {
        return ownerRepository.update(owner);
    }

    @Override
    public boolean deleteById(Integer id) {
        return ownerRepository.deleteById(id);
    }

    @Override
    public Collection<Owner> findAll() {
        return ownerRepository.findAll();
    }

    @Override
    public Optional<Owner> findByUserId(Integer userId) {
        return ownerRepository.findByUserId(userId);
    }

    public String getOwnerNameIfExist(Integer userId) {
        return ownerRepository.findByUserId(userId).map(Owner::getName).orElse("");
    }
}
