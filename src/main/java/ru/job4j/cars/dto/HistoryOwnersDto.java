package ru.job4j.cars.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class HistoryOwnersDto {
    private String ownerName;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date historyStartAt;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date historyEndAt;
}
