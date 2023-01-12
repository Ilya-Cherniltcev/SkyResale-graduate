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
    @Mapping(target = "image", expression = "java(getImageLink(ads.getImage()))")
    AdsDto toDto(Ads ads);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "adsComments", ignore = true)

    @Mapping(target = "image", source = "adsImage")
    Ads createAds(CreateAdsDto createAdsDto, User author, AdsImage adsImage);

    @Mapping(target = "description", source = "createAdsDto.description")
    @Mapping(target = "price", source = "createAdsDto.price")
    @Mapping(target = "title", source = "createAdsDto.title")
    Ads updAds(CreateAdsDto createAdsDto,Ads ads);


    default String getImageLink(AdsImage adsImage) {
        return "/ads/image/" + adsImage.getId();
    }

}
