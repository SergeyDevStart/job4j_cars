package ru.job4j.cars.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class SearchDto {
    String brand;
    String body;
    String engine;
    String gearbox;
    String typeDrive;
}
