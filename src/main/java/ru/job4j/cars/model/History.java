package ru.job4j.cars.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Entity
@Table(name = "history")
public class History {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    LocalDateTime startAt;
    LocalDateTime endAt;

    @Override
    public String toString() {
        return String.format("start: %s - end: %s",
                FORMATTER.format(startAt), FORMATTER.format(endAt));
    }
}
