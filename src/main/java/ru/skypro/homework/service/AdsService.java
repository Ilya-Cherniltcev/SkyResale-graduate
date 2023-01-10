package ru.skypro.homework.service;

import ru.skypro.homework.dto.AdsCommentDto;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateAdsDto;
import ru.skypro.homework.model.Ads;

import java.util.Collection;
import java.util.List;

public interface AdsService {

    Collection<AdsDto> getALLAds();

    AdsDto createAds(CreateAdsDto adsDto);

    Collection<AdsDto> getAdsMe();

    List<AdsCommentDto> getAdsComments(long adsId);

    AdsCommentDto createAdsComments(long id, AdsCommentDto adsCommentDto);

    AdsDto deleteAdsComments(String adsId, long id);

    AdsCommentDto updateAdsComments(String adPk, long id, AdsCommentDto adsCommentDto);

    Ads removeAds(long id);

    Ads getAds(long id);

    AdsDto updateAds(long id, CreateAdsDto adsDto);
}
