package ru.job4j.cars.repository.post;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.dto.SearchDto;
import ru.job4j.cars.model.*;
import ru.job4j.cars.repository.CrudRepository;

import javax.persistence.EntityGraph;
import javax.persistence.Subgraph;
import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;

@AllArgsConstructor
@Repository
@Slf4j
public class HibernatePostRepository implements PostRepository {
    private final CrudRepository crudRepository;

    @Override
    public Optional<Post> create(Post post) {
        try {
            crudRepository.run(session -> session.persist(post));
            return Optional.of(post);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public boolean delete(Post post) {
        try {
            crudRepository.run(session -> session.delete(post));
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean update(Post post) {
        try {
            crudRepository.run(session -> session.merge(post));
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public Optional<Post> findById(Integer id) {
        Function<Session, Optional<Post>> command = session -> {
            EntityGraph<Post> entityGraph = session.createEntityGraph(Post.class);
            entityGraph.addAttributeNodes("files", "priceHistories", "car");
            Subgraph<Car> carSubgraph = entityGraph.addSubgraph("car");
            carSubgraph.addAttributeNodes("engine", "owner", "historyOwners");
            Subgraph<HistoryOwners> historyOwnersSubgraph = carSubgraph.addSubgraph("historyOwners");
            historyOwnersSubgraph.addAttributeNodes("owner");
            var sq = session.createQuery(
                    "FROM Post WHERE id = :id", Post.class)
                    .setParameter("id", id)
                    .setHint("javax.persistence.fetchgraph", entityGraph);
            return sq.uniqueResultOptional();
        };
        return crudRepository.tx(command);
    }

    @Override
    public Optional<Post> findPostWithFilesById(Integer id) {
        return crudRepository.optional(
                """
                        FROM Post post
                        LEFT JOIN FETCH post.files
                        WHERE post.id = :id
                        """,
                Post.class,
                Map.of("id", id)
        );
    }

    @Override
    public Collection<Post> findAll() {
        Function<Session, Collection<Post>> command = session -> {
            EntityGraph<Post> entityGraph = session.createEntityGraph(Post.class);
            entityGraph.addAttributeNodes("files", "priceHistories");
            var sq = session.createQuery(
                            "SELECT DISTINCT post FROM Post post", Post.class)
                    .setHint("javax.persistence.fetchgraph", entityGraph);
            return sq.getResultList();
        };
        return crudRepository.tx(command);
    }

    @Override
    public Collection<Post> findPostsWithFile() {
        return crudRepository.query(
                """
                        SELECT DISTINCT post
                        FROM Post post
                        LEFT JOIN FETCH post.files
                        LEFT JOIN FETCH post.priceHistories
                        WHERE post.files IS NOT EMPTY
                        """,
                Post.class
        );
    }

    @Override
    public Collection<Post> findAllLastDay() {
        return crudRepository.query(
                """
                    SELECT DISTINCT post
                    FROM Post post
                    LEFT JOIN FETCH post.files
                    LEFT JOIN FETCH post.priceHistories
                    WHERE post.created > :yesterday
                    """,
                Post.class,
                Map.of("yesterday", LocalDate.now().atStartOfDay())
        );
    }

    @Override
    public Collection<Post> findByBrand(String brand) {
        try {
            Brand enumBrand = Brand.valueOf(brand.toUpperCase());
            return crudRepository.query(
                    "FROM Post p WHERE p.brand = :brand",
                    Post.class,
                    Map.of("brand", enumBrand)
            );
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid brand: " + brand, e);
        }
    }

    @Override
    public List<Post> findSearchResult(SearchDto searchDto) {
        Function<Session, List<Post>> command = session -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Post> query = cb.createQuery(Post.class);
            Root<Post> post = query.from(Post.class);
            Join<Post, Car> carJoin = post.join("car");
            Join<Car, Engine> engineJoin = carJoin.join("engine");
            post.fetch("files", JoinType.LEFT);
            post.fetch("priceHistories", JoinType.LEFT);
            List<Predicate> predicates = new ArrayList<>();
            if (searchDto.getBrand() != null && !searchDto.getBrand().isEmpty()) {
                predicates.add(cb.equal(post.get("brand"), Brand.valueOf(searchDto.getBrand())));
            }
            if (searchDto.getBody() != null && !searchDto.getBody().isEmpty()) {
                predicates.add(cb.equal(carJoin.get("body"), Body.valueOf(searchDto.getBody())));
            }
            if (searchDto.getGearbox() != null && !searchDto.getGearbox().isEmpty()) {
                predicates.add(cb.equal(carJoin.get("gearbox"), Gearbox.valueOf(searchDto.getGearbox())));
            }
            if (searchDto.getTypeDrive() != null && !searchDto.getTypeDrive().isEmpty()) {
                predicates.add(cb.equal(carJoin.get("typeDrive"), TypeDrive.valueOf(searchDto.getTypeDrive())));
            }
            if (searchDto.getEngine() != null && !searchDto.getEngine().isEmpty()) {
                predicates.add(cb.equal(engineJoin.get("name"), searchDto.getEngine()));
            }
            query.select(post).distinct(true).where(predicates.toArray(new Predicate[0]));
            return session.createQuery(query).getResultList();
        };
        return crudRepository.tx(command);
    }
}
