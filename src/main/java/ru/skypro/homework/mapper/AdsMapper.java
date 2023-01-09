package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateAdsDto;
import ru.skypro.homework.model.Ads;
import ru.skypro.homework.model.AdsImage;
import ru.skypro.homework.model.User;

@Mapper (componentModel = "spring")
public interface AdsMapper {
   @Mapping(source = "id", target = "adsId")
   @Mapping(target = "image", expression = "java(\"/image/\" + ads.getImage().getId())")
    AdsDto toDto(Ads ads);
    Ads toAds(AdsDto adsDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "adsComments", ignore = true)
    Ads fromCreateAds(CreateAdsDto createAdsDto, User author, AdsImage image);

}
