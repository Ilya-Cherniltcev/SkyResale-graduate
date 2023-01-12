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
import ru.skypro.homework.dto.CreateAdsDto;
import ru.skypro.homework.exception.AdsImageNotFoundException;
import ru.skypro.homework.exception.SaveFileException;
import ru.skypro.homework.mapper.AdsCommentMapper;
import ru.skypro.homework.mapper.ImageMapper;
import ru.skypro.homework.mapper.AdsMapper;
import ru.skypro.homework.model.Ads;
import ru.skypro.homework.model.AdsComment;
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
        Ads ads = getAds(adsId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUser(authentication.getName());
        if (!ads.getAuthor().equals(user) && !userService.isAdmin(authentication)) {
            log.warn("Unavailable to update. It's not your ads! ads author = {}, username = {}", ads.getAuthor().getEmail(), user.getEmail());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unavailable to update. It's not your ads!");
        }

        Ads updatedAds = adsMapper.updAds(adsDto, ads);

        updatedAds.setAdsComments(List.copyOf(ads.getAdsComments()));
//        updatedAds.setId(id);
        log.info("The ad with id = {} was updated ", adsId);
        return adsMapper.toDto(adsRepository.save(updatedAds));
    }


    @Transactional
    @Override

    public Ads removeAds(long adsId) {
        Ads adsForRemove = getAds(adsId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUser(authentication.getName());
        if (!adsForRemove.getAuthor().equals(user) && !userService.isAdmin(authentication)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Недоступно для удаления, это не ваше объявление");
        }
        adsRepository.deleteById(adsId);
        return adsForRemove;
    }

    @Override
    public Ads getAds(long id) {
        Ads ads = adsRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("The ad with id = {} does not exist", id);
                    return new NotFoundException("Ad with id = " + id + " does not exist");
                });
        log.info("The ad with id = {} was found", id);

        return ads;

    }


    @Override
    public List<AdsCommentDto> getAdsComments(long adsId) {
        return adsCommentRepository.findAdsCommentByAdsId(adsId).stream()
                .map(adsCommentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public AdsCommentDto createAdsComments(long adsId, AdsCommentDto adsCommentDto) {
        AdsComment comment = adsCommentMapper.toAdsComment(adsCommentDto);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails principalUser = (UserDetails) authentication.getPrincipal();
        User user = userService.getUser(principalUser.getUsername());

        comment.setAuthor(user);
        comment.setAdsId(adsRepository.findAdsById(adsId));
        comment.setCreatedAt(OffsetDateTime.now());
        return adsCommentMapper.toDto(adsCommentRepository.save(comment));
    }

    @Override
    public AdsCommentDto deleteAdsComments(long adsId, long commentId) {
        AdsComment comment = getCommentsIfPresent(adsId,commentId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails principalUser = (UserDetails) authentication.getPrincipal();
        User user = userService.getUser(principalUser.getUsername());
        Ads ads = getAds(adsId);

        if (!comment.getAuthor().equals(user) && !ads.getAuthor().equals(user) && !userService.isAdmin(authentication)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unavailable to delete. It's not your comment!");
        }
        adsCommentRepository.deleteById(commentId);
        return adsCommentMapper.toDto(comment);
    }

    @Override
    public AdsCommentDto updateAdsComments(long adsId, long commentId, AdsCommentDto adsCommentDto) {
        Ads ads = getAds(adsId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails principalUser = (UserDetails) authentication.getPrincipal();
        User user = userService.getUser(principalUser.getUsername());
        AdsComment comment = getCommentsIfPresent(adsId, commentId);

        if (!comment.getAuthor().equals(user) && !userService.isAdmin(authentication)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unavailable to update. It's not your comment!");
        }
        AdsComment comm = adsCommentMapper.toAdsComment(adsCommentDto);
        comm.setAuthor(user);
        comm.setAdsId(ads);
        comm.setId(commentId);
        comm.setCreatedAt(OffsetDateTime.now());
        adsCommentRepository.save(comm);
        return adsCommentMapper.toDto(comm);
    }

    private List<AdsDto> toAdsDtoList(List<Ads> ads) {
        return ads.stream()
                .map(adsMapper::toDto)
                .collect(Collectors.toList());
    }

    private AdsComment getCommentsIfPresent(long adsId, long commentId) {
        AdsComment adsComment= adsCommentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий с указанным идентификатором " + commentId + " does not exist"));
        if (!adsComment.getAdsId().getId().equals(adsId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Comment is not from this ads!");
        }
        return adsComment;
    }

    @Override
    public byte[] getImage(Long adsImageId) {
        return adsImageRepository.findAdsImageById(adsImageId)
                .map(AdsImage::getData)
                .orElseThrow(AdsImageNotFoundException::new);
    }


}
