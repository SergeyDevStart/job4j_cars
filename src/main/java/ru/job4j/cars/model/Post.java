package ru.job4j.cars.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"user", "priceHistories", "files", "car", "participates"})
@Table(name = "auto_post")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Post {
    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String description;
    private boolean status;
    private LocalDateTime created = LocalDateTime.now().withSecond(0);

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auto_user_id")
    private User user;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "auto_post_id")
    @Builder.Default
    private Set<PriceHistory> priceHistories = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<File> files = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private Brand brand;

    @JoinColumn(name = "car_id", foreignKey = @ForeignKey(name = "CAR_ID_FK"))
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE, CascadeType.MERGE})
    private Car car;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    @Builder.Default
    private Set<Participates> participates = new HashSet<>();

    public void addFile(File file) {
        files.add(file);
        file.setPost(this);
    }
}
