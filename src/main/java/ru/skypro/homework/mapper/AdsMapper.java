package ru.skypro.homework.mapper;

import org.mapstruct.Mapping;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateAdsDto;
import ru.skypro.homework.model.Ads;
import ru.skypro.homework.model.AdsImage;
import ru.skypro.homework.model.User;

import java.util.ArrayList;
import java.util.List;

public interface AdsMapper {
    @Mapping(source = "id", target = "adsId")
    @Mapping(target = "image", expression = "java(fromImageList(ads.getImage).get(0))")
    AdsDto toDto(Ads ads);

    Ads toAds(AdsDto adsDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "adsComments", ignore = true)
//    Ads fromCreateAds(CreateAdsDto createAdsDto, User author, AdsImage image);
    Ads fromCreateAds(CreateAdsDto createAdsDto, User author);

    default List<String> fromImageList(List<AdsImage> adsImages) {
        List<String> strings = new ArrayList<>();
        adsImages.forEach(i -> strings.add("/image" + i.getId()));
        return strings;
    }

}
