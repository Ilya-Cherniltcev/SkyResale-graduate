package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.model.Ads;

@Mapper
public interface AdsMapper {
    @Mapping(source = "id", target = "adsId")
    AdsDto toDto(Ads ads);

    Ads toAds(AdsDto adsDto);
}
