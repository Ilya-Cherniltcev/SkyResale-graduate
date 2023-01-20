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
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
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

    @Transactional
    @Override
    public ResponseWrapperAdsDto getAllAds() {
        log.info("try to get all ads");
        List<AdsDto> adsDtoList = toAdsDtoList(adsRepository.findAll());
        return responseWrapperAdsMapper.toResponseWrapperAdsDto(adsDtoList.size(), adsDtoList);
    }

    @Override
    public AdsDto createAds(CreateAdsDto adsDto, MultipartFile[] files) {
        log.info("try to add ads");
        User user = userService.getUserFromAuthentication();
        log.info("try to get user from authentication");
        try {
            Ads newAds = adsRepository.save(adsMapper.createAds(adsDto, user));

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
            log.warn("ads hasn't saved");
            throw new SaveFileException();
        }
    }

    @Transactional
    @Override
    public ResponseWrapperAdsDto getAdsMe() {
        log.info("try to get all ads of one user");
        User user = userService.getUserFromAuthentication();
        List<AdsDto> adsDtoList = toAdsDtoList(
                adsRepository.findAll().stream()
                        .filter(e -> e.getAuthor().equals(user))
                        .collect(Collectors.toList()));
        return responseWrapperAdsMapper.toResponseWrapperAdsDto(adsDtoList.size(), adsDtoList);
    }

    @Transactional
    @Override
    public AdsDto updateAds(long adsId, CreateAdsDto adsDto) {
        log.info("try to update ads");
        checkAdsDtoNeededFieldsIsNotNull(adsDto);
        Ads ads = findAds(adsId);
        log.info("try to find ads by id");
        User user = userService.getUserFromAuthentication();
        checkThisIsYourAdsOrYouAdmin(ads, user);
        Ads updatedAds = adsMapper.updAds(adsDto, ads);
        log.info("The ad with id = {} was updated ", adsId);
        return adsMapper.toDto(adsRepository.save(updatedAds));
    }

    @Transactional
    @Override
    public AdsDto updateAdsImage(long adsId, MultipartFile file) {
        log.info("try to update ads image");
        Ads ads = findAds(adsId);
        log.info("try to find ads by id");
        checkThisIsYourAdsOrYouAdmin(ads, userService.getUserFromAuthentication());
        try {
            AdsImage newAdsImage = imageMapper.toAdsImage(file);
            adsImageRepository.deleteAdsImagesByAds(ads);
            newAdsImage.setAds(ads);
            adsImageRepository.save(newAdsImage);
        } catch (IOException e) {
            log.warn("unable to save image");
            throw new SaveFileException();
        }
        Ads updAds = findAds(adsId);
        return adsMapper.toDto(updAds);
    }

    @Transactional
    @Override
    public AdsDto removeAds(long adsId) {
        log.info("try to remove ads if it's found by id");
        Ads adsForRemove = findAds(adsId);
        User user = userService.getUserFromAuthentication();
        checkThisIsYourAdsOrYouAdmin(adsForRemove, user);
        adsRepository.deleteById(adsId);
        return adsMapper.toDto(adsForRemove);
    }

    @Transactional
    @Override
    public FullAdsDto getFullAds(long adsId) {
        return adsMapper.toFullAdsDto(findAds(adsId));
    }

    @Transactional
    @Override
    public ResponseWrapperCommentDto getAdsComments(long adsId) {
        log.info("try to get ads comments");
        List<AdsCommentDto> adsCommentDtoList = adsCommentRepository.findAdsCommentByAds(findAds(adsId)).stream()
                .map(adsCommentMapper::toDto)
                .sorted(Comparator.comparing(AdsCommentDto::getCreatedAt))
                .collect(Collectors.toList());
        return responseWrapperAdsCommentMapper.toResponseWrapperCommentDto(adsCommentDtoList.size(), adsCommentDtoList);
    }

    @Transactional
    @Override
    public AdsCommentDto createAdsComments(long adsId, AdsCommentDto adsCommentDto) {
        log.info("try to create comment for found by id ads");
        AdsComment comment = adsCommentMapper.toAdsComment(adsCommentDto);
        User user = userService.getUserFromAuthentication();

        comment.setAuthor(user);
        comment.setAds(findAds(adsId));
        comment.setCreatedAt(OffsetDateTime.now());
        return adsCommentMapper.toDto(adsCommentRepository.save(comment));
    }

    @Transactional
    @Override
    public AdsCommentDto getAdsComment(long adsId, long commentId) {
        log.info("try to get comment for ads by comment id and ads id");
        return adsCommentMapper.toDto(getCommentsIfPresent(adsId, commentId));
    }

    @Transactional
    @Override
    public AdsCommentDto deleteAdsComments(long adsId, long commentId) {
        log.info("try to remove comment for ads by comment id and ads id");
        AdsComment comment = getCommentsIfPresent(adsId, commentId);
        User user = userService.getUserFromAuthentication();
        checkThisIsYourCommentOrYouAdmin(comment, user);
        adsCommentRepository.deleteById(commentId);
        return adsCommentMapper.toDto(comment);
    }

    @Transactional
    @Override
    public AdsCommentDto updateAdsComments(long adsId, long commentId, AdsCommentDto adsCommentDto) {
        log.info("try to remove comment for ads by comment id and ads id");
        checkAdsCommentDtoTextIsNotNull(adsCommentDto);
        User user = userService.getUserFromAuthentication();
        AdsComment comment = findAdsComment(commentId);
        checkThisIsYourCommentOrYouAdmin(comment, user);

        AdsComment comm = adsCommentMapper.toAdsComment(adsCommentDto);
        comm.setAuthor(user);
        comm.setAds(findAds(adsId));
        comm.setId(commentId);
        comm.setCreatedAt(OffsetDateTime.now());
        log.info("comment updated");
        return adsCommentMapper.toDto(adsCommentRepository.save(comm));
    }

    /**
     * Convert List {@link Ads} to List {@link AdsDto}
     * @param ads List of Ads
     * @return Converted List
     */
    @Transactional
    protected List<AdsDto> toAdsDtoList(List<Ads> ads) {
        return ads.stream()
                .map(adsMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public byte[] getImage(UUID adsImageUuid) {
        return adsImageRepository.findAdsImageByUuid(adsImageUuid)
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
        log.info("try to find ads by ads id");
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
        log.info("try to find comment by comment id");
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
