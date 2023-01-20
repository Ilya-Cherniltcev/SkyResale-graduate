package ru.skypro.homework.service;

import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.exception.*;
import ru.skypro.homework.model.Ads;
import ru.skypro.homework.model.AdsComment;
import ru.skypro.homework.model.AdsImage;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.AdsCommentRepository;
import ru.skypro.homework.repository.AdsImageRepository;
import ru.skypro.homework.repository.AdsRepository;

import java.util.UUID;

public interface AdsService {

    /**
     * Get all {@link Ads} from {@link AdsRepository}
     * @return Collection of needed ads with quantity of ads in this collection
     */
    ResponseWrapperAdsDto getAllAds();

    /**
     * Create {@link Ads} in {@link AdsRepository} and create {@link AdsImage} in {@link AdsImageRepository}
     * @param adsDto data for create Ads
     * @param files images for Ads
     * @return Created Ads
     * @throws UserNotFoundException – if User not found in database
     * @throws SaveFileException if something went wrong, when image updated
     */
    AdsDto createAds(CreateAdsDto adsDto, MultipartFile[] files);

    /**
     * Get all {@link Ads} from {@link AdsRepository} which belong to current {@link User}
     * @return Collection of needed ads with quantity of ads in this collection
     * @throws UserNotFoundException – if User not found in database
     */
    ResponseWrapperAdsDto getAdsMe();

    /**
     * Update {@link Ads} in {@link AdsRepository}
     * @param adsId Id of needed Ads
     * @param adsDto data for update
     * @return Updated Ads
     * @throws NoContentException if fields of current Dto are empty
     * @throws ItIsNotYourAdsException if current User hasn't right for doing something with this Ads
     */
    AdsDto updateAds(long adsId, CreateAdsDto adsDto);

    /**
     * Update {@link AdsImage} in {@link AdsImageRepository}
     * @param adsId Id of needed Ads
     * @param file uploaded file
     * @return Updated Ads
     * @throws SaveFileException if something went wrong, when image updated
     */
    AdsDto updateAdsImage(long adsId, MultipartFile file);

    /**
     * Delete {@link Ads} from {@link AdsRepository}<br>
     * Delete {@link AdsComment} which belong to this Ads from {@link AdsCommentRepository}<br>
     * Delete {@link AdsImage} which belong to this Ads from {@link AdsImageRepository}
     * @param adsId Id of needed Ads
     * @return Deleted Ads
     * @throws ItIsNotYourAdsException if current User hasn't right for doing something with this Ads
     */
    AdsDto removeAds(long adsId);

    /**
     * Get full data about needed {@link Ads}
     * @param adsId Id of needed Ads
     * @return Full needed data
     * @throws AdsNotFoundException if Ads with adsId not found in database
     */
    FullAdsDto getFullAds(long adsId);

    /**
     * Get all {@link AdsComment} for needed{@link Ads} from {@link AdsCommentRepository}
     * @param adsId Id of needed Ads
     * @return Collection of comment for needed Ads
     */
    ResponseWrapperCommentDto getAdsComments(long adsId);

    /**
     * Create {@link AdsComment} in {@link AdsCommentRepository}
     * @param adsId Id of needed Ads
     * @param adsCommentDto Data for create
     * @return Created comment
     * @throws UserNotFoundException – if current User not found in database
     */
    AdsCommentDto createAdsComments(long adsId, AdsCommentDto adsCommentDto);

    /**
     * Get {@link AdsComment} from {@link AdsCommentRepository}
     * @param adsId Id of needed Ads
     * @param commentId Id of needed comment
     * @return Needed comment
     */
    AdsCommentDto getAdsComment(long adsId, long commentId);

    /**
     * Delete {@link AdsComment} from {@link AdsCommentRepository}
     * @param adsId Id of needed Ads
     * @param commentId Id of needed comment
     * @return Deleted comment
     * @throws ItIsNotYourCommentException if current User hasn't right for doing something with this comment
     */
    AdsCommentDto deleteAdsComments(long adsId, long commentId);

    /**
     * Update {@link AdsComment} in {@link AdsCommentRepository}
     * @param adsId Id of needed Ads
     * @param commentId Id of needed comment
     * @param adsCommentDto Data for update
     * @return Updated comment
     * @throws NoContentException if field "text" of current Dto are empty
     * @throws ItIsNotYourCommentException if current User hasn't right for doing something with this comment
     */
    AdsCommentDto updateAdsComments(long adsId, long commentId, AdsCommentDto adsCommentDto);

    /**
     * Get array of byte of {@link AdsImage} from {@link AdsImageRepository}
     * @param adsImageUuid UUID in database
     * @return AdsImage
     * @throws AdsImageNotFoundException if AdsImage not found in database
     */
    byte[] getImage(UUID adsImageUuid);

}
