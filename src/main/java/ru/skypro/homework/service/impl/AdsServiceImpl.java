package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.webjars.NotFoundException;
import ru.skypro.homework.dto.AdsCommentDto;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateAdsCommentDto;
import ru.skypro.homework.dto.CreateAdsDto;
import ru.skypro.homework.exception.AdsCommentNotFoundException;
import ru.skypro.homework.exception.AdsImageNotFoundException;
import ru.skypro.homework.exception.AdsNotFoundException;
import ru.skypro.homework.exception.SaveFileException;
import ru.skypro.homework.mapper.AdsCommentMapper;
import ru.skypro.homework.mapper.ImageMapper;
import ru.skypro.homework.mapper.AdsMapper;
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
import java.util.Collection;
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
    private final ImageMapper imageMapper;
    private final AdsCommentMapper adsCommentMapper;

    @Override
    public Collection<AdsDto> getAllAds() {
        List<Ads> allAds = adsRepository.findAll();
        if (allAds.isEmpty()) {
            throw new NotFoundException("Ничего не найдено");
        }
        return toAdsDtoList(allAds);

    }

    @Override
    public AdsDto createAds(CreateAdsDto ads, MultipartFile file) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUser(authentication.getName());
        try {
            AdsImage adsImage = imageMapper.toAdsImage(file);

            adsImageRepository.save(adsImage);
            Ads newAds = adsMapper.createAds(ads, user, adsImage);

            Ads response = adsRepository.save(newAds);

            return adsMapper.toDto(response);
        } catch (IOException e) {
            throw new SaveFileException();
        }
    }

    @Override
    public Collection<AdsDto> getAdsMe() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUser(authentication.getName());
        return toAdsDtoList(
                adsRepository.findAll().stream()
                        .filter(e -> e.getAuthor().equals(user))
                        .collect(Collectors.toList()));
    }

    @Transactional
    @Override
    public AdsDto updateAds(long adsId, CreateAdsDto adsDto) {
        Ads ads = findAds(adsId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUser(authentication.getName());
        if (!ads.getAuthor().equals(user) && !userService.isAdmin(authentication)) {
            log.warn("Unavailable to update. It's not your ads! ads author = {}, username = {}", ads.getAuthor().getEmail(), user.getEmail());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unavailable to update. It's not your ads!");
        }

        Ads updatedAds = adsMapper.updAds(adsDto, ads);

        log.info("The ad with id = {} was updated ", adsId);
        return adsMapper.toDto(adsRepository.save(updatedAds));
    }


    @Transactional
    @Override
    public AdsDto removeAds(long adsId) {
        Ads adsForRemove = findAds(adsId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUser(authentication.getName());
        if (!adsForRemove.getAuthor().equals(user) && !userService.isAdmin(authentication)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Недоступно для удаления, это не ваше объявление");
        }
        List<Long> listOfId = adsCommentRepository.findAdsCommentByAds(adsForRemove).stream()
                .map(AdsComment::getId)
                .collect(Collectors.toList());

        for (Long aLong : listOfId) {
            adsCommentRepository.deleteById(aLong);
        }

        adsRepository.deleteById(adsId);
        return adsMapper.toDto(adsForRemove);
    }

    @Transactional
    @Override
    public AdsDto getAdsById(long adsId) {
        return adsMapper.toDto(findAds(adsId));
    }


    @Transactional
    @Override
    public List<AdsCommentDto> getAdsComments(long adsId) {
        return adsCommentRepository.findAdsCommentByAds(findAds(adsId)).stream()
                .map(adsCommentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public AdsCommentDto createAdsComments(long adsId, CreateAdsCommentDto adsCommentDto) {
        AdsComment comment = adsCommentMapper.createToAdsComment(adsCommentDto);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails principalUser = (UserDetails) authentication.getPrincipal();
        User user = userService.getUser(principalUser.getUsername());

        comment.setAuthor(user);
        comment.setAds(findAds(adsId));
        comment.setCreatedAt(OffsetDateTime.now());
        return adsCommentMapper.toDto(adsCommentRepository.save(comment));
    }

    @Transactional
    @Override
    public AdsCommentDto deleteAdsComments(long adsId, long commentId) {
        AdsComment comment = getCommentsIfPresent(adsId, commentId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails principalUser = (UserDetails) authentication.getPrincipal();
        User user = userService.getUser(principalUser.getUsername());
        Ads ads = findAds(adsId);

        if (!comment.getAuthor().equals(user) && !ads.getAuthor().equals(user) && !userService.isAdmin(authentication)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unavailable to delete. It's not your comment!");
        }
        adsCommentRepository.deleteById(commentId);
        return adsCommentMapper.toDto(comment);
    }

    @Transactional
    @Override
    public AdsCommentDto updateAdsComments(long adsId, long commentId, CreateAdsCommentDto adsCommentDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails principalUser = (UserDetails) authentication.getPrincipal();
        User user = userService.getUser(principalUser.getUsername());
        AdsComment comment = findAdsComment(commentId);

        if (!comment.getAuthor().equals(user) && !userService.isAdmin(authentication)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unavailable to update. It's not your comment!");
        }
        AdsComment comm = adsCommentMapper.createToAdsComment(adsCommentDto);
        comm.setAuthor(user);
        comm.setAds(findAds(adsId));
        comm.setId(commentId);
        comm.setCreatedAt(OffsetDateTime.now());
        return adsCommentMapper.toDto(adsCommentRepository.save(comm));
    }

    private List<AdsDto> toAdsDtoList(List<Ads> ads) {
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
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Comment is not from this ads!");
        }
        return adsComment;
    }
}
