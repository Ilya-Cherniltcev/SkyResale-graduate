package ru.skypro.homework.service.impl;

import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.service.AdsService;

import java.util.Collection;

@Service
public class AdsServiceImpl implements AdsService {
    @Override
    public Collection<AdsDto> getALLAds() {
        return null;
    }

    @Override
    public AdsDto createAds(AdsDto adsDto) {
        return null;
    }

    @Override
    public Collection<AdsDto> getAdsMe() {
        return null;
    }

    @Override
    public String getAdsComments(String ad_pk) {
        return null;
    }

    @Override
    public AdsDto createAdsComments(long id, String comment) {
        return null;
    }

    @Override
    public AdsDto deleteAdsComments(String ad_pk, long id) {
        return null;
    }

    @Override
    public AdsDto updateAdsComments(long id, String adsComment) {
        return null;
    }

    @Override
    public AdsDto removeAds(long id) {
        return null;
    }

    @Override
    public AdsDto getAds(long id) {
        return null;
    }

    @Override
    public AdsDto updateAds(AdsDto adsDto) {
        return null;
    }
}
