package ru.job4j.cars.repository.historyowners;

import ru.job4j.cars.model.HistoryOwners;

import java.util.Optional;

public interface HistoryOwnersRepository {
    Optional<HistoryOwners> save(HistoryOwners historyOwners);
}
