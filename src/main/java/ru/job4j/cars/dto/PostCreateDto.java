package ru.job4j.cars.dto;

import lombok.*;
import ru.job4j.cars.model.User;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class PostCreateDto {
    private User user;
    private String brand;
    private LocalDateTime created = LocalDateTime.now().withSecond(0);
    private String carName;
    private long price;
    private String description;
    private String body;
    private String gearbox;
    private String typeDrive;
    private Integer engineId;
}
