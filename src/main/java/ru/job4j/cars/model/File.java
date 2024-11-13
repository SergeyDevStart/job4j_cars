package ru.job4j.cars.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@Entity
@Table(name = "files")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;
    @JoinColumn(name = "name")
    private String name;
    @JoinColumn(name = "path")
    private String path;

    public File(String name, String path) {
        this.name = name;
        this.path = path;
    }
}
