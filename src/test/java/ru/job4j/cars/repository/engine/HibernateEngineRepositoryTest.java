package ru.job4j.cars.repository.engine;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;
import ru.job4j.cars.model.Engine;
import ru.job4j.cars.repository.CrudRepository;

import java.util.List;

class HibernateEngineRepositoryTest {
    private static SessionFactory sessionFactory;
    private static EngineRepository engineRepository;

    @BeforeAll
    static void init() {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure().build();
        sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        engineRepository = new HibernateEngineRepository(new CrudRepository(sessionFactory));
    }

    @AfterAll
    static void sfClose() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @AfterEach
    void clearTable() {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.createQuery("DELETE FROM Engine").executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }

    @Test
    void whenSaveEngineThenHasSameEngine() {
        var engine = new Engine();
        engine.setName("engine");
        engineRepository.save(engine);
        Engine result = engineRepository.findById(engine.getId()).get();
        assertThat(result).isEqualTo(engine);
    }

    @Test
    void whenEngineWasUpdateSuccessfully() {
        var engine = new Engine();
        engine.setName("engine");
        engineRepository.save(engine);
        engine.setName("updatedEngine");
        engineRepository.update(engine);
        assertThat(engineRepository.findById(engine.getId()).get().getName())
                .isEqualTo(engine.getName());
    }

    @Test
    void whenEngineWasDeleteSuccessfully() {
        var engine = new Engine();
        engine.setName("engine");
        engineRepository.save(engine);
        assertThat(engineRepository.deleteById(engine.getId())).isTrue();
    }

    @Test
    void whenFindAllThenGetSameList() {
        var engine1 = new Engine();
        engine1.setName("engine1");
        engineRepository.save(engine1);
        var engine2 = new Engine();
        engine2.setName("engine2");
        engineRepository.save(engine2);
        List<Engine> expected = List.of(engine1, engine2);
        assertThat(engineRepository.findAll()).isEqualTo(expected);
    }
}