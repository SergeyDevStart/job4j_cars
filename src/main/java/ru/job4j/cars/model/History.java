package ru.job4j.cars.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Entity
@Table(name = "history")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class History {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;
    private LocalDateTime startAt;
    private LocalDateTime endAt;

    @Override
    public String toString() {
        return String.format("start: %s - end: %s",
                FORMATTER.format(startAt), FORMATTER.format(endAt));
    }
}
