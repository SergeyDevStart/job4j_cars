package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import ru.job4j.cars.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class UserRepository {
    private final SessionFactory sessionFactory;

    public User create(User user) {
        var session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.save(user);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
        }
        session.close();
        return user;
    }

    public void update(User user) {
        var session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.createQuery(
                    """
                    UPDATE User SET login = :login, password = :password
                    WHERE id = :id
                    """)
                    .setParameter("login", user.getLogin())
                    .setParameter("password", user.getPassword())
                    .setParameter("id", user.getId())
                    .executeUpdate();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
        }
        session.close();
    }

    public void delete(int userId) {
        var session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.createQuery("DELETE User WHERE id = :id")
                    .setParameter("id", userId)
                    .executeUpdate();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
        }
        session.close();
    }

    public List<User> findAllOrderById() {
        var session = sessionFactory.openSession();
        List<User> result = new ArrayList<>();
        try {
            session.beginTransaction();
            result = session.createQuery("from User ORDER by id", User.class).list();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
        }
        session.close();
        return result;
    }

    public Optional<User> findById(int userId) {
        var session = sessionFactory.openSession();
        Optional<User> optionalUser = Optional.empty();
        try {
            session.beginTransaction();
            Query<User> query = session.createQuery("from User u WHERE u.id = :id", User.class);
            query.setParameter("id", userId);
            optionalUser = Optional.of(query.getSingleResult());
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
        }
        session.close();
        return optionalUser;
    }

    public List<User> findByLikeLogin(String key) {
        var session = sessionFactory.openSession();
        List<User> result = new ArrayList<>();
        try {
            session.beginTransaction();
            Query<User> query = session.createQuery("from User u WHERE u.login LIKE :key", User.class);
            query.setParameter("key", "%" + key + "%");
            result = query.list();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
        }
        session.close();
        return result;
    }

    public Optional<User> findByLogin(String login) {
        var session = sessionFactory.openSession();
        Optional<User> optionalUser = Optional.empty();
        try {
            session.beginTransaction();
            Query<User> query = session.createQuery("from User u WHERE u.login = :login", User.class);
            query.setParameter("login", login);
            optionalUser = Optional.of(query.getSingleResult());
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
        }
        session.close();
        return optionalUser;
    }
}
