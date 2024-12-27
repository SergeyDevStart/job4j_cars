package ru.job4j.cars.dto;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class PostCardDto {
    private Integer id;
    private String brand;
    private Integer fileId;
    private long price;
    private LocalDateTime created;
    private boolean status;
}
