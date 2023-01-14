package ru.skypro.homework.service;

import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.AdsCommentDto;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateAdsCommentDto;
import ru.skypro.homework.dto.CreateAdsDto;

import java.util.Collection;
import java.util.List;

public interface AdsService {

    Collection<AdsDto> getAllAds();

    AdsDto createAds(CreateAdsDto adsDto, MultipartFile[] files);

    AdsDto removeAds(long id);

    AdsDto getAdsById(long adsId);

    AdsDto updateAds(long id, CreateAdsDto adsDto);

    Collection<AdsDto> getAdsMe();

    List<AdsCommentDto> getAdsComments(long adsId);

    AdsCommentDto createAdsComments(long adsId, CreateAdsCommentDto adsCommentDto);

    AdsCommentDto deleteAdsComments(long adsId, long commentId);

    AdsCommentDto updateAdsComments(long adsId, long commentId, CreateAdsCommentDto adsCommentDto);
    byte[] getImage(Long adsImageId);
}
