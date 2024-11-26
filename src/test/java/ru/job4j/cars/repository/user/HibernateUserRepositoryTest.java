package ru.job4j.cars.repository.user;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.User;
import ru.job4j.cars.repository.CrudRepository;

import static org.assertj.core.api.Assertions.assertThat;

class HibernateUserRepositoryTest {
    private static SessionFactory sf;
    private static UserRepository userRepository;

    @BeforeAll
    static void init() {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure().build();
        sf = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        userRepository = new HibernateUserRepository(new CrudRepository(sf));
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
            session.createQuery("DELETE FROM User").executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }

    @Test
    void whenSaveUserThenGetSame() {
        User user = new User();
        user.setLogin("login");
        user.setPassword("pass");
        userRepository.save(user);
        var result = userRepository.findById(user.getId()).get();
        assertThat(result).isEqualTo(user);
    }

    @Test
    void whenFindByLoginAndPasswordThenGetSame() {
        User user = new User();
        user.setLogin("login");
        user.setPassword("pass");
        userRepository.save(user);
        var result = userRepository.findByLoginAndPassword("login", "pass").get();
        assertThat(result).isEqualTo(user);
    }
}