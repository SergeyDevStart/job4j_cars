package ru.job4j.cars.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.job4j.cars.dto.PostCardDto;
import ru.job4j.cars.dto.PostCreateDto;
import ru.job4j.cars.model.File;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.model.PriceHistory;

import java.util.*;

@Mapper(componentModel = "spring")
public interface PostMapper {
    @Mapping(source = "brand", target = "brand", qualifiedByName = "toStringFromEnum")
    @Mapping(source = "files", target = "fileId", qualifiedByName = "toFileIdFromFiles")
    @Mapping(source = "priceHistories", target = "price", qualifiedByName = "toPriceFromPriceHistories")
    PostCardDto toPostCardDtoFromPost(Post post);

    @Mapping(source = "price", target = "priceHistories", qualifiedByName = "toPriceHistoryFromPrice")
    @Mapping(source = "engineId", target = "car.engine.id")
    @Mapping(source = "ownerName", target = "car.owner.name")
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
    default long toPriceFromPriceHistories(Set<PriceHistory> priceHistories) {
        List<PriceHistory> sortedList = new ArrayList<>(priceHistories);
        sortedList.sort(Comparator.comparing(PriceHistory::getCreated));
        return sortedList.isEmpty() ? 0 : sortedList.get(sortedList.size() - 1).getAfter();
    }

    @Named("toPriceHistoryFromPrice")
    default Set<PriceHistory> toPriceHistoryFromPrice(long price) {
        var priceHistory = new PriceHistory();
        priceHistory.setBefore(0);
        priceHistory.setAfter(price);
        Set<PriceHistory> priceHistories = new HashSet<>();
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
