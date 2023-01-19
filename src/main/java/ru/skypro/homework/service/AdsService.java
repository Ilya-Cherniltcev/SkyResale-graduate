package ru.skypro.homework.service;

import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;

import java.util.UUID;

public interface AdsService {

    ResponseWrapperAdsDto getAllAds();

    AdsDto createAds(CreateAdsDto adsDto, MultipartFile[] files);

    AdsDto removeAds(long id);

    FullAdsDto getFullAds(long adsId);

    AdsDto updateAds(long adsId, CreateAdsDto adsDto);
    AdsDto updateAdsImage(long adsId, MultipartFile file);

    ResponseWrapperAdsDto getAdsMe();

    ResponseWrapperCommentDto getAdsComments(long adsId);

    AdsCommentDto createAdsComments(long adsId, AdsCommentDto adsCommentDto);

    AdsCommentDto getAdsComment(long adsId, long commentId);

    AdsCommentDto deleteAdsComments(long adsId, long commentId);

    AdsCommentDto updateAdsComments(long adsId, long commentId, AdsCommentDto adsCommentDto);
    byte[] getImage(UUID adsImageId);

}
