package ru.job4j.cars.model;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "history_owners")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"owner", "car"})
public class HistoryOwners {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "car_id")
    private Car car;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "owner_id")
    private Owner owner;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startAt;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endAt;

    public void setCar(Car car) {
        this.car = car;
        this.car.getHistoryOwners().add(this);
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
        this.owner.getHistoryOwners().add(this);
    }
}

