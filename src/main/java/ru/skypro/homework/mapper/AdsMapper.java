package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateAdsDto;
import ru.skypro.homework.model.Ads;
import ru.skypro.homework.model.AdsImage;
import ru.skypro.homework.model.User;

@Mapper
public interface AdsMapper {
   @Mapping(source = "id", target = "adsId")
   @Mapping(target = "image", expression = "java(\"/image/\" + ads.getImage().getId())")
    AdsDto toDto(Ads ads);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "adsComments", ignore = true)
    default Ads fromCreateAds(CreateAdsDto createAdsDto, User author, AdsImage image) {
        return null;
    }

}
