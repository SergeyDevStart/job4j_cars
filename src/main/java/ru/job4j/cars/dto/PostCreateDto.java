package ru.job4j.cars.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.job4j.cars.model.User;

import java.time.LocalDateTime;
import java.util.Date;

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
    private String ownerName;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date historyStartAt;
}
