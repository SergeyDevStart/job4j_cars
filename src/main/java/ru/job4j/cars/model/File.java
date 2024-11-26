package ru.job4j.cars.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "files")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;
    private String name;
    @EqualsAndHashCode.Include
    private String path;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auto_post_id")
    private Post post;

    public File(String name, String path) {
        this.name = name;
        this.path = path;
    }
}
