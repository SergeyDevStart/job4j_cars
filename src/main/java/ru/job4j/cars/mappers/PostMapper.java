package ru.job4j.cars.mappers;

import lombok.AllArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.job4j.cars.dto.PostCardDto;
import ru.job4j.cars.dto.PostCreateDto;
import ru.job4j.cars.model.Engine;
import ru.job4j.cars.model.File;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.model.PriceHistory;
import ru.job4j.cars.repository.engine.EngineRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface PostMapper {
    @Mapping(source = "brand", target = "brand", qualifiedByName = "toStringFromEnum")
    @Mapping(source = "files", target = "fileId", qualifiedByName = "toFileIdFromFiles")
    @Mapping(source = "priceHistories", target = "price", qualifiedByName = "toPriceFromPriceHistories")
    PostCardDto toPostCardDtoFromPost(Post post);

    @Mapping(source = "price", target = "priceHistories", qualifiedByName = "toPriceHistoryFromPrice")
    @Mapping(source = "engineId", target = "car.engine.id")
    @Mapping(source = "carName", target = "car.name")
    @Mapping(target = "brand", expression = "java(toEnumFromString(postCreateDto.getBrand(), ru.job4j.cars.model.Brand.class))")
    @Mapping(target = "car.body", expression = "java(toEnumFromString(postCreateDto.getBody(), ru.job4j.cars.model.Body.class))")
    @Mapping(target = "car.gearbox", expression = "java(toEnumFromString(postCreateDto.getGearbox(), ru.job4j.cars.model.Gearbox.class))")
    @Mapping(target = "car.typeDrive", expression = "java(toEnumFromString(postCreateDto.getTypeDrive(), ru.job4j.cars.model.TypeDrive.class))")
    Post toPostFromPostCreateDto(PostCreateDto postCreateDto);

    @Named("toFileIdFromFiles")
    default Integer toFileIdFromFiles(Set<File> files) {
        return files.stream()
                .findFirst()
                .map(File::getId)
                .orElse(null);
    }

    @Named("toPriceFromPriceHistories")
    default long toPriceFromPriceHistories(List<PriceHistory> priceHistories) {
        return priceHistories.isEmpty() ? 0 : priceHistories.get(priceHistories.size() - 1).getAfter();
    }

    @Named("toPriceHistoryFromPrice")
    default List<PriceHistory> toPriceHistoryFromPrice(long price) {
        var priceHistory = new PriceHistory();
        priceHistory.setBefore(0);
        priceHistory.setAfter(price);
        List<PriceHistory> priceHistories = new ArrayList<>();
        priceHistories.add(priceHistory);
        return priceHistories;
    }

    default <E extends Enum<E>> E toEnumFromString(String value, Class<E> enumType) {
        if (value == null) {
            return null;
        }
        try {
            return Enum.valueOf(enumType, value);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid value for enum ");
        }
    }

    @Named("toStringFromEnum")
    default String toStringFromEnum(Enum<?> enumValue) {
        return enumValue == null ? null : enumValue.name();
    }
}
