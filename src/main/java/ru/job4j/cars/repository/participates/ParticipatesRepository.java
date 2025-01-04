package ru.job4j.cars.repository.participates;

import ru.job4j.cars.model.Participates;

import java.util.Optional;

public interface ParticipatesRepository {
    Optional<Participates> save(Participates participates);
}
