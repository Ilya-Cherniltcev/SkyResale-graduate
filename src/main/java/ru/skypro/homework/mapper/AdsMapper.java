package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateAdsDto;
import ru.skypro.homework.dto.FullAdsDto;
import ru.skypro.homework.model.Ads;
import ru.skypro.homework.model.AdsImage;
import ru.skypro.homework.model.User;

import java.util.List;

@Mapper
public interface AdsMapper {
    @Mapping(target = "pk", source = "id")
    @Mapping(target = "author", source = "ads.author.id")
    @Mapping(target = "image", expression = "java(getImageLink(ads.getImage()))")
    AdsDto toDto(Ads ads);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "adsComments", ignore = true)
    @Mapping(target = "image", ignore = true)
    Ads createAds(CreateAdsDto createAdsDto, User author);

    @Mapping(target = "description", source = "createAdsDto.description")
    @Mapping(target = "price", source = "createAdsDto.price")
    @Mapping(target = "title", source = "createAdsDto.title")
    Ads updAds(CreateAdsDto createAdsDto, Ads ads);

    @Mapping(target = "pk", source = "id")
    @Mapping(target = "authorFirstName", source = "ads.author.firstName")
    @Mapping(target = "authorLastName", source = "ads.author.lastName")
    @Mapping(target = "email", source = "ads.author.login")
    @Mapping(target = "phone", source = "ads.author.phoneNumber")
    @Mapping(target = "image", expression = "java(getImageLink(ads.getImage()))")
    FullAdsDto toFullAdsDto(Ads ads);

@Transactional
    default String[] getImageLink(List<AdsImage> adsImages) {
        String[] arrayLinks = new String[adsImages.size()];
        for (int i = 0; i < adsImages.size(); i++) {
            arrayLinks[i] = "/ads/image/" + adsImages.get(i).getId();
        }
        return arrayLinks;
    }

}
