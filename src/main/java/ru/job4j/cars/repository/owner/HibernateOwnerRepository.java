package ru.job4j.cars.repository.owner;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Owner;
import ru.job4j.cars.repository.CrudRepository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
@Slf4j
public class HibernateOwnerRepository implements OwnerRepository {
    private final CrudRepository crudRepository;

    @Override
    public Optional<Owner> save(Owner owner) {
        try {
            crudRepository.run(session -> session.persist(owner));
            return Optional.of(owner);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Owner> findById(Integer id) {
        return crudRepository.optional(
                "FROM Owner WHERE id = :id",
                Owner.class,
                Map.of("id", id)
        );
    }

    @Override
    public boolean update(Owner owner) {
        try {
            crudRepository.run(session -> session.merge(owner));
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean deleteById(Integer id) {
        return crudRepository.executeUpdate(
                "DELETE Owner WHERE id = :id",
                Map.of("id", id)
        );
    }

    @Override
    public Collection<Owner> findAll() {
        return crudRepository.query(
                "FROM Owner",
                Owner.class
        );
    }

    @Override
    public Optional<Owner> findByUserId(Integer userId) {
        return crudRepository.optional(
                """
                FROM Owner owner
                JOIN FETCH owner.user
                JOIN FETCH owner.historyOwners
                WHERE owner.user.id = :userId
                """,
                Owner.class,
                Map.of("userId", userId)
        );
    }
}
