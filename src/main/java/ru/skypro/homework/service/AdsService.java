package ru.skypro.homework.service;

import ru.skypro.homework.dto.AdsDto;

import java.util.Collection;

public interface AdsService {

    Collection<AdsDto> getALLAds();

    AdsDto createAds(AdsDto adsDto);

    Collection<AdsDto>  getAdsMe();

    String getAdsComments(String ad_pk);

    AdsDto createAdsComments(long id, String comment);

    AdsDto deleteAdsComments(String ad_pk, long id);

    AdsDto updateAdsComments(long id, String adsComment);

    AdsDto removeAds(long id);

    AdsDto getAds(long id);

    AdsDto updateAds(AdsDto adsDto);
}
