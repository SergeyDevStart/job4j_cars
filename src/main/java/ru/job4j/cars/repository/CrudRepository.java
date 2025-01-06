package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@Repository
@Slf4j
@AllArgsConstructor
public class CrudRepository {
    private final SessionFactory sf;

    public void run(Consumer<Session> command) {
        tx(session -> {
                    command.accept(session);
                    return null;
                }
        );
    }

    public void run(String query, Map<String, Object> args) {
        Consumer<Session> command = session -> {
            var sq = session
                    .createQuery(query);
            setQueryParameters(sq, args);
            sq.executeUpdate();
        };
        run(command);
    }

    public <T> Optional<T> optional(String query, Class<T> cl, Map<String, Object> args) {
        Function<Session, Optional<T>> command = session -> {
            var sq = session
                    .createQuery(query, cl);
            setQueryParameters(sq, args);
            return sq.uniqueResultOptional();
        };
        return tx(command);
    }

    public <T> List<T> query(String query, Class<T> cl) {
        Function<Session, List<T>> command = session -> session
                .createQuery(query, cl)
                .list();
        return tx(command);
    }

    public <T> List<T> query(String query, Class<T> cl, Map<String, Object> args) {
        Function<Session, List<T>> command = session -> {
            var sq = session
                    .createQuery(query, cl);
            setQueryParameters(sq, args);
            return sq.list();
        };
        return tx(command);
    }

    public boolean executeUpdate(String query, Map<String, Object> args) {
        Function<Session, Boolean> command = session -> {
            var sq = session.createQuery(query);
            setQueryParameters(sq, args);
            return sq.executeUpdate() > 0;
        };
        return tx(command);
    }

    private void setQueryParameters(Query<?> query, Map<String, Object> args) {
        for (Map.Entry<String, Object> arg : args.entrySet()) {
            query.setParameter(arg.getKey(), arg.getValue());
        }
    }

    public <T> T tx(Function<Session, T> command) {
        Transaction transaction = null;
        try (Session session = sf.openSession()) {
            transaction = session.beginTransaction();
            T result = command.apply(session);
            transaction.commit();
            return result;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            log.error("Transaction failed: ", e);
            throw e;
        }
    }
}
