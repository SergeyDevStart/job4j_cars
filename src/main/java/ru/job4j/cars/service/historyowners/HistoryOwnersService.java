package ru.job4j.cars.service.historyowners;

import ru.job4j.cars.model.Car;
import ru.job4j.cars.model.HistoryOwners;
import ru.job4j.cars.model.Owner;
import ru.job4j.cars.model.Post;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

public interface HistoryOwnersService {
    Optional<HistoryOwners> save(HistoryOwners historyOwners);

    void saveHistoryOwnersInPost(Post post, Date historyStartAt);

    HistoryOwners createHistoryOwners(Car car, Owner owner, LocalDate startAt, LocalDate endAt);
}
