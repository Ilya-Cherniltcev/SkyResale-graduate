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

    @Transactional
    @Override
    public ResponseWrapperAdsDto getAllAds() {
        List<AdsDto> adsDtoList = toAdsDtoList(adsRepository.findAll());
        return responseWrapperAdsMapper.toResponseWrapperAdsDto(adsDtoList.size(), adsDtoList);
    }

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

    @Transactional
    @Override
    public AdsDto updateAds(long adsId, CreateAdsDto adsDto) {
        testAdsDtoNeededFieldsIsNotNull(adsDto);
        Ads ads = findAds(adsId);
        User user = userService.getUserFromAuthentication();
        testThisIsYourAdsOrYouAdmin(ads, user);

        Ads updatedAds = adsMapper.updAds(adsDto, ads);

        log.info("The ad with id = {} was updated ", adsId);
        return adsMapper.toDto(adsRepository.save(updatedAds));
    }


    @Transactional
    @Override
    public AdsDto removeAds(long adsId) {
        Ads adsForRemove = findAds(adsId);
        User user = userService.getUserFromAuthentication();
        testThisIsYourAdsOrYouAdmin(adsForRemove, user);

        adsCommentRepository.deleteAdsCommentsByAds(adsForRemove);
        adsImageRepository.deleteAdsImagesByAds(adsForRemove);

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
        List<AdsCommentDto> adsCommentDtoList = adsCommentRepository.findAdsCommentByAds(findAds(adsId)).stream()
                .map(adsCommentMapper::toDto)
                .collect(Collectors.toList());
        return responseWrapperAdsCommentMapper.toResponseWrapperCommentDto(adsCommentDtoList.size(), adsCommentDtoList);
    }

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

    @Override
    public AdsCommentDto getAdsComment(long adsId, long commentId) {
        return adsCommentMapper.toDto(getCommentsIfPresent(adsId, commentId));
    }

    @Transactional
    @Override
    public AdsCommentDto deleteAdsComments(long adsId, long commentId) {
        AdsComment comment = getCommentsIfPresent(adsId, commentId);
        User user = userService.getUserFromAuthentication();
        testThisIsYourCommentOrYouAdmin(comment, user);
        adsCommentRepository.deleteById(commentId);
        return adsCommentMapper.toDto(comment);
    }

    @Transactional
    @Override
    public AdsCommentDto updateAdsComments(long adsId, long commentId, AdsCommentDto adsCommentDto) {
        testAdsCommentDtoTextIsNotNull(adsCommentDto);
        User user = userService.getUserFromAuthentication();
        AdsComment comment = findAdsComment(commentId);
        testThisIsYourCommentOrYouAdmin(comment, user);

        AdsComment comm = adsCommentMapper.toAdsComment(adsCommentDto);
        comm.setAuthor(user);
        comm.setAds(findAds(adsId));
        comm.setId(commentId);
        comm.setCreatedAt(OffsetDateTime.now());
        return adsCommentMapper.toDto(adsCommentRepository.save(comm));
    }

    @Transactional
    public List<AdsDto> toAdsDtoList(List<Ads> ads) {
        return ads.stream()
                .map(adsMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public byte[] getImage(Long adsImageId) {
        return adsImageRepository.findAdsImageById(adsImageId)
                .map(AdsImage::getData)
                .orElseThrow(AdsImageNotFoundException::new);
    }

    private Ads findAds(long adsId) {
        return adsRepository.findAdsById(adsId)
                .orElseThrow(AdsNotFoundException::new);
    }

    private AdsComment findAdsComment(long commentId) {
        return adsCommentRepository.findAdsCommentById(commentId)
                .orElseThrow(AdsCommentNotFoundException::new);
    }

    private AdsComment getCommentsIfPresent(long adsId, long commentId) {
        AdsComment adsComment = findAdsComment(commentId);
        if (!adsComment.getAds().getId().equals(adsId)) {
            throw new CommentFromAnotherAdsException();
        }
        return adsComment;
    }

    private void testAdsCommentDtoTextIsNotNull(AdsCommentDto adsCommentDto) {
        if (adsCommentDto.getCommentText() == null) {
            throw new NoContentException();
        }
    }

    private void testAdsDtoNeededFieldsIsNotNull(CreateAdsDto adsDto) {
        if (adsDto.getDescription() == null || adsDto.getPrice() == null || adsDto.getTitle() == null) {
            throw new NoContentException();
        }
    }

    private void testThisIsYourCommentOrYouAdmin(AdsComment comment, User user) {
        if (!comment.getAuthor().equals(user) && !userService.isAdmin()) {
            throw new ItIsNotYourCommentException();
        }
    }

    private void testThisIsYourAdsOrYouAdmin(Ads ads, User user) {
        if (!ads.getAuthor().equals(user) && !userService.isAdmin()) {
            log.warn("Unavailable to update. It's not your ads! ads author = {}, login = {}", ads.getAuthor().getLogin(), user.getLogin());
            throw new ItIsNotYourAdsException();
        }
    }
}
