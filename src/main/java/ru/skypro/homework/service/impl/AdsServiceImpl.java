package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.exception.*;
import ru.skypro.homework.mapper.*;
import ru.skypro.homework.model.Ads;
import ru.skypro.homework.model.AdsComment;
import ru.skypro.homework.model.AdsImage;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.AdsCommentRepository;
import ru.skypro.homework.repository.AdsImageRepository;
import ru.skypro.homework.repository.AdsRepository;
import ru.skypro.homework.service.AdsService;
import ru.skypro.homework.service.UserService;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdsServiceImpl implements AdsService {
    private final UserService userService;
    private final AdsRepository adsRepository;
    private final AdsImageRepository adsImageRepository;
    private final AdsCommentRepository adsCommentRepository;
    private final AdsMapper adsMapper;
    private final ResponseWrapperAdsMapper responseWrapperAdsMapper;
    private final ResponseWrapperCommentMapper responseWrapperAdsCommentMapper;
    private final ImageMapper imageMapper;
    private final AdsCommentMapper adsCommentMapper;
    /**
     * Get all {@link Ads} from {@link AdsRepository}
     * @return Collection of needed ads with quantity of ads in this collection
     */
    @Transactional
    @Override
    public ResponseWrapperAdsDto getAllAds() {
        List<AdsDto> adsDtoList = toAdsDtoList(adsRepository.findAll());
        return responseWrapperAdsMapper.toResponseWrapperAdsDto(adsDtoList.size(), adsDtoList);
    }

    /**
     * Create {@link Ads} in {@link AdsRepository} and create {@link AdsImage} in {@link AdsImageRepository}
     * @param ads data for create Ads
     * @param files images for Ads
     * @return Created Ads
     * @throws UserNotFoundException – if User not found in database
     * @throws SaveFileException if something went wrong, when image updated
     */
    @Override
    public AdsDto createAds(CreateAdsDto ads, MultipartFile[] files) {
        User user = userService.getUserFromAuthentication();
        try {
            Ads newAds = adsRepository.save(adsMapper.createAds(ads, user));

            List<AdsImage> adsImageList = new ArrayList<>();
            for (MultipartFile file : files) {
                AdsImage newAdsImage = imageMapper.toAdsImage(file);
                newAdsImage.setAds(newAds);
                adsImageRepository.save(newAdsImage);
                adsImageList.add(newAdsImage);
            }
            Ads updAds = findAds(newAds.getId());
            updAds.setImage(adsImageList);
            Ads response = adsRepository.save(updAds);

            return adsMapper.toDto(response);
        } catch (IOException e) {
            throw new SaveFileException();
        }
    }

    /**
     * Get all {@link Ads} from {@link AdsRepository} which belong to current {@link User}
     * @return Collection of needed ads with quantity of ads in this collection
     * @throws UserNotFoundException – if User not found in database
     */
    @Transactional
    @Override
    public ResponseWrapperAdsDto getAdsMe() {
        User user = userService.getUserFromAuthentication();
        List<AdsDto> adsDtoList = toAdsDtoList(
                adsRepository.findAll().stream()
                        .filter(e -> e.getAuthor().equals(user))
                        .collect(Collectors.toList()));
        return responseWrapperAdsMapper.toResponseWrapperAdsDto(adsDtoList.size(), adsDtoList);
    }

    /**
     * Update {@link Ads} in {@link AdsRepository}
     * @param adsId Id of needed Ads
     * @param adsDto data for update
     * @return Updated Ads
     * @throws NoContentException if fields of current Dto are empty
     * @throws ItIsNotYourAdsException if current User hasn't right for doing something with this Ads
     */
    @Transactional
    @Override
    public AdsDto updateAds(long adsId, CreateAdsDto adsDto) {
        checkAdsDtoNeededFieldsIsNotNull(adsDto);
        Ads ads = findAds(adsId);
        User user = userService.getUserFromAuthentication();
        checkThisIsYourAdsOrYouAdmin(ads, user);

        Ads updatedAds = adsMapper.updAds(adsDto, ads);

        log.info("The ad with id = {} was updated ", adsId);
        return adsMapper.toDto(adsRepository.save(updatedAds));
    }

    /**
     * Update {@link AdsImage} in {@link AdsImageRepository}
     * @param adsId Id of needed Ads
     * @param file uploaded file
     * @return Updated Ads
     * @throws SaveFileException if something went wrong, when image updated
     */
    @Transactional
    @Override
    public AdsDto updateAdsImage(long adsId, MultipartFile file) {
        Ads ads = findAds(adsId);
        checkThisIsYourAdsOrYouAdmin(ads, userService.getUserFromAuthentication());
        try {
            AdsImage newAdsImage = imageMapper.toAdsImage(file);
            adsImageRepository.deleteAdsImagesByAds(ads);
            newAdsImage.setAds(ads);
            adsImageRepository.save(newAdsImage);
        } catch (IOException e) {
            throw new SaveFileException();
        }
        Ads updads = findAds(adsId);
        return adsMapper.toDto(updads);
    }

    /**
     * Delete {@link Ads} from {@link AdsRepository}<br>
     * Delete {@link AdsComment} which belong to this Ads from {@link AdsCommentRepository}<br>
     * Delete {@link AdsImage} which belong to this Ads from {@link AdsImageRepository}
     * @param adsId Id of needed Ads
     * @return Deleted Ads
     * @throws ItIsNotYourAdsException if current User hasn't right for doing something with this Ads
     */
    @Transactional
    @Override
    public AdsDto removeAds(long adsId) {
        Ads adsForRemove = findAds(adsId);
        User user = userService.getUserFromAuthentication();
        checkThisIsYourAdsOrYouAdmin(adsForRemove, user);

        adsCommentRepository.deleteAdsCommentsByAds(adsForRemove);
        adsImageRepository.deleteAdsImagesByAds(adsForRemove);

        adsRepository.deleteById(adsId);
        return adsMapper.toDto(adsForRemove);
    }

    /**
     * Get full data about needed {@link Ads}
     * @param adsId Id of needed Ads
     * @return Full needed data
     * @throws AdsNotFoundException if Ads with adsId not found in database
     */
    @Transactional
    @Override
    public FullAdsDto getFullAds(long adsId) {
        return adsMapper.toFullAdsDto(findAds(adsId));
    }

    /**
     * Get all {@link AdsComment} for needed{@link Ads} from {@link AdsCommentRepository}
     * @param adsId Id of needed Ads
     * @return Collection of comment for needed Ads
     */
    @Transactional
    @Override
    public ResponseWrapperCommentDto getAdsComments(long adsId) {
        List<AdsCommentDto> adsCommentDtoList = adsCommentRepository.findAdsCommentByAds(findAds(adsId)).stream()
                .map(adsCommentMapper::toDto)
                .collect(Collectors.toList());
        return responseWrapperAdsCommentMapper.toResponseWrapperCommentDto(adsCommentDtoList.size(), adsCommentDtoList);
    }

    /**
     * Create {@link AdsComment} in {@link AdsCommentRepository}
     * @param adsId Id of needed Ads
     * @param adsCommentDto Data for create
     * @return Created comment
     * @throws UserNotFoundException – if current User not found in database
     */
    @Transactional
    @Override
    public AdsCommentDto createAdsComments(long adsId, AdsCommentDto adsCommentDto) {
        AdsComment comment = adsCommentMapper.toAdsComment(adsCommentDto);
        User user = userService.getUserFromAuthentication();

        comment.setAuthor(user);
        comment.setAds(findAds(adsId));
        comment.setCreatedAt(OffsetDateTime.now());
        return adsCommentMapper.toDto(adsCommentRepository.save(comment));
    }

    /**
     * Get {@link AdsComment} from {@link AdsCommentRepository}
     * @param adsId Id of needed Ads
     * @param commentId Id of needed comment
     * @return Needed comment
     */
    @Transactional
    @Override
    public AdsCommentDto getAdsComment(long adsId, long commentId) {
        return adsCommentMapper.toDto(getCommentsIfPresent(adsId, commentId));
    }

    /**
     * Delete {@link AdsComment} from {@link AdsCommentRepository}
     * @param adsId Id of needed Ads
     * @param commentId Id of needed comment
     * @return Deleted comment
     * @throws ItIsNotYourCommentException if current User hasn't right for doing something with this comment
     */
    @Transactional
    @Override
    public AdsCommentDto deleteAdsComments(long adsId, long commentId) {
        AdsComment comment = getCommentsIfPresent(adsId, commentId);
        User user = userService.getUserFromAuthentication();
        checkThisIsYourCommentOrYouAdmin(comment, user);
        adsCommentRepository.deleteById(commentId);
        return adsCommentMapper.toDto(comment);
    }

    /**
     * Update {@link AdsComment} in {@link AdsCommentRepository}
     * @param adsId Id of needed Ads
     * @param commentId Id of needed comment
     * @param adsCommentDto Data for update
     * @return Updated comment
     * @throws NoContentException if field "text" of current Dto are empty
     * @throws ItIsNotYourCommentException if current User hasn't right for doing something with this comment
     */
    @Transactional
    @Override
    public AdsCommentDto updateAdsComments(long adsId, long commentId, AdsCommentDto adsCommentDto) {
        checkAdsCommentDtoTextIsNotNull(adsCommentDto);
        User user = userService.getUserFromAuthentication();
        AdsComment comment = findAdsComment(commentId);
        checkThisIsYourCommentOrYouAdmin(comment, user);

        AdsComment comm = adsCommentMapper.toAdsComment(adsCommentDto);
        comm.setAuthor(user);
        comm.setAds(findAds(adsId));
        comm.setId(commentId);
        comm.setCreatedAt(OffsetDateTime.now());
        return adsCommentMapper.toDto(adsCommentRepository.save(comm));
    }

    /**
     * Convert List {@link Ads} to List {@link AdsDto}
     * @param ads List of Ads
     * @return Converted List
     */
    @Transactional
    public List<AdsDto> toAdsDtoList(List<Ads> ads) {
        return ads.stream()
                .map(adsMapper::toDto)
                .collect(Collectors.toList());
    }
    /**
     * Get array of byte of {@link AdsImage} from {@link AdsImageRepository}
     * @param adsImageId Id in database
     * @return AdsImage
     * @throws AdsImageNotFoundException if AdsImage not found in database
     */
    @Transactional
    @Override
    public byte[] getImage(Long adsImageId) {
        return adsImageRepository.findAdsImageById(adsImageId)
                .map(AdsImage::getData)
                .orElseThrow(AdsImageNotFoundException::new);
    }

    /**
     * Find Ads by adsId in {@link AdsRepository}
     * @param adsId Id of book in database
     * @return founded Ads
     * @throws AdsNotFoundException if Ads with adsId not found in database
     */
    @Transactional
    protected Ads findAds(long adsId) {
        return adsRepository.findAdsById(adsId)
                .orElseThrow(AdsNotFoundException::new);
    }
    /**
     * Find comment by commentId in {@link AdsCommentRepository}
     * @param commentId Id of comment in database
     * @throws AdsCommentNotFoundException if comment with commentId not found in database
     * @return Founded comment
     */
    @Transactional
    protected AdsComment findAdsComment(long commentId) {
        return adsCommentRepository.findAdsCommentById(commentId)
                .orElseThrow(AdsCommentNotFoundException::new);
    }

    /**
     * Get {@link AdsComment} if it belongs to needed {@link Ads}
     * @param adsId Id of needed Ads
     * @param commentId Id of needed comment
     * @return needed comment
     * @throws CommentFromAnotherAdsException if comment which need from other Ads
     * @throws AdsCommentNotFoundException if ads by id is not exist
     */
    @Transactional
    protected AdsComment getCommentsIfPresent(long adsId, long commentId) {
        AdsComment adsComment = findAdsComment(commentId);
        if (!adsComment.getAds().getId().equals(adsId)) {
            throw new CommentFromAnotherAdsException();
        }
        return adsComment;
    }

    /**
     * Check text of {@link AdsCommentDto} for update. Fields must be {@link NotNull}
     * @param adsCommentDto current Dto for update
     * @throws NoContentException if field "text" of current Dto are empty
     */
    private void checkAdsCommentDtoTextIsNotNull(AdsCommentDto adsCommentDto) {
        if (adsCommentDto.getCommentText() == null) {
            throw new NoContentException();
        }
    }

    /**
     * Check fields of current {@link CreateAdsDto} for update. Fields must be {@link NotNull}
     * @param adsDto current Dto for update
     * @throws NoContentException if fields of current Dto are empty
     */
    private void checkAdsDtoNeededFieldsIsNotNull(CreateAdsDto adsDto) {
        if (adsDto.getDescription() == null || adsDto.getPrice() == null || adsDto.getTitle() == null) {
            throw new NoContentException();
        }
    }

    /**
     * Check the rights for actions with {@link AdsComment}
     * @param comment comment to do something with (for example: update, delete)
     * @param user current User
     * @throws ItIsNotYourCommentException if current User hasn't right for doing something with this comment
     */
    private void checkThisIsYourCommentOrYouAdmin(AdsComment comment, User user) {
        if (!comment.getAuthor().equals(user) && !userService.isAdmin()) {
            throw new ItIsNotYourCommentException();
        }
    }

    /**
     * Check the rights for actions with {@link Ads}
     * @param ads Ads to do something with (for example: update, delete)
     * @param user current User
     * @throws ItIsNotYourAdsException if current User hasn't right for doing something with this Ads
     */
    private void checkThisIsYourAdsOrYouAdmin(Ads ads, User user) {
        if (!ads.getAuthor().equals(user) && !userService.isAdmin()) {
            log.warn("Unavailable to update. It's not your ads! ads author = {}, login = {}", ads.getAuthor().getLogin(), user.getLogin());
            throw new ItIsNotYourAdsException();
        }
    }
}
