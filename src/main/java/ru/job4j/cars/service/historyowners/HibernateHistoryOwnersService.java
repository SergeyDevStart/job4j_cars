package ru.job4j.cars.service.historyowners;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.model.Car;
import ru.job4j.cars.model.HistoryOwners;
import ru.job4j.cars.model.Owner;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.repository.historyowners.HistoryOwnersRepository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@Service
@AllArgsConstructor
public class HibernateHistoryOwnersService implements HistoryOwnersService {
    private final HistoryOwnersRepository historyOwnersRepository;

    @Override
    public Optional<HistoryOwners> save(HistoryOwners historyOwners) {
        return historyOwnersRepository.save(historyOwners);
    }

    @Override
    public void saveHistoryOwnersInPost(Post post, Date historyStartAt) {
        if (historyStartAt == null) {
            throw new IllegalArgumentException("History start date cannot be null");
        }
        Car car = post.getCar();
        Owner owner = car.getOwner();
        owner.setUser(post.getUser());
        var startAt = historyStartAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        var endAt = LocalDate.now(ZoneId.systemDefault());
        var historyOwners = createHistoryOwners(car, owner, startAt, endAt);
        historyOwnersRepository.save(historyOwners);
    }

    @Override
    public HistoryOwners createHistoryOwners(Car car, Owner owner, LocalDate startAt, LocalDate endAt) {
        HistoryOwners historyOwners = new HistoryOwners();
        historyOwners.setCar(car);
        historyOwners.setOwner(owner);
        historyOwners.setStartAt(startAt);
        historyOwners.setEndAt(endAt);
        return historyOwners;
    }
}
