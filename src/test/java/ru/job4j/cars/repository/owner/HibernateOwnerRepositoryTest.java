package ru.job4j.cars.repository.owner;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.Owner;
import ru.job4j.cars.model.User;
import ru.job4j.cars.repository.CrudRepository;

import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class HibernateOwnerRepositoryTest {
    private static SessionFactory sf;
    private static OwnerRepository ownerRepository;
    private static CrudRepository crudRepository;

    @BeforeAll
    static void init() {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure().build();
        sf = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        crudRepository = new CrudRepository(sf);
        ownerRepository = new HibernateOwnerRepository(crudRepository);
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
            session.createQuery("DELETE FROM Owner").executeUpdate();
            session.createQuery("DELETE FROM User").executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }

    private Owner getOwner() {
        User user = new User();
        user.setLogin("login");
        user.setPassword("password");
        crudRepository.run(session -> session.save(user));
        return Owner.builder()
                .name("Name")
                .user(user)
                .historyOwners(new HashSet<>())
                .build();
    }

    @Test
    void whenSaveOwnerThenHasSame() {
        Owner owner = getOwner();
        ownerRepository.save(owner);
        assertThat(ownerRepository.findById(owner.getId()).get()).isEqualTo(owner);
    }

    @Test
    void whenOwnerWasUpdateSuccessfully() {
        Owner owner = getOwner();
        ownerRepository.save(owner);
        owner.setName("newName");
        ownerRepository.update(owner);
        var result = ownerRepository.findById(owner.getId()).get();
        assertThat(result).isEqualTo(owner);
        assertThat(result.getName()).isEqualTo(owner.getName());
    }

    @Test
    void whenOwnerWasDeleteSuccessfully() {
        Owner owner = getOwner();
        ownerRepository.save(owner);
        assertThat(ownerRepository.deleteById(owner.getId())).isTrue();
        assertThat(ownerRepository.findById(owner.getId())).isEmpty();
    }

    @Test
    void whenFindAllThenHasSame() {
        Owner owner1 = getOwner();
        Owner owner2 = getOwner();
        ownerRepository.save(owner1);
        ownerRepository.save(owner2);
        List<Owner> expected = List.of(owner1, owner2);
        assertThat(ownerRepository.findAll()).isEqualTo(expected);
    }
}