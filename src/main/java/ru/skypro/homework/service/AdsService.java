package ru.skypro.homework.service;

import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.AdsCommentDto;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateAdsDto;
import ru.skypro.homework.model.Ads;

import java.util.Collection;
import java.util.List;

public interface AdsService {

    Collection<AdsDto> getAllAds();


    AdsDto createAds(CreateAdsDto adsDto, MultipartFile file);


    Collection<AdsDto> getAdsMe();

    List<AdsCommentDto> getAdsComments(long adsId);

    AdsCommentDto createAdsComments(long adsId, AdsCommentDto adsCommentDto);

    AdsCommentDto deleteAdsComments(long adsId, long commentId);

    AdsCommentDto updateAdsComments(long adsId, long commentId, AdsCommentDto adsCommentDto);

    Ads removeAds(long id);

    Ads getAds(long id);

    AdsDto updateAds(long id, CreateAdsDto adsDto);
    byte[] getImage(Long adsImageId);
}
