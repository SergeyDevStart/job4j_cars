package ru.job4j.cars.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"user", "priceHistories", "files", "car", "participates"})
@Entity
@Table(name = "auto_post")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Post {
    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String description;
    private boolean status;
    private LocalDateTime created = LocalDateTime.now().withSecond(0);

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "auto_post_id")
    private List<PriceHistory> priceHistories = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "auto_post_id")
    private Set<File> files = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private Brand brand;

    @JoinColumn(name = "car_id", foreignKey = @ForeignKey(name = "CAR_ID_FK"))
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Car car;

    @OneToMany(mappedBy = "post")
    private Set<Participant> participates = new HashSet<>();
}
