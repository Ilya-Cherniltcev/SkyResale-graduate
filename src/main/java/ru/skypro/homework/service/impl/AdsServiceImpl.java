package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.webjars.NotFoundException;
import ru.skypro.homework.dto.AdsCommentDto;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateAdsDto;
import ru.skypro.homework.mapper.AdsCommentMapper;
import ru.skypro.homework.mapper.AdsMapper;
import ru.skypro.homework.model.Ads;
import ru.skypro.homework.model.AdsComment;
import ru.skypro.homework.model.AdsImage;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.AdsCommentRepository;
import ru.skypro.homework.repository.AdsRepository;
import ru.skypro.homework.service.AdsService;
import ru.skypro.homework.service.UserService;

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
    private final AdsMapper adsMapper;
    private final AdsCommentRepository adsCommentRepository;
    private final AdsCommentMapper adsCommentMapper;

    @Override
    public Collection<AdsDto> getALLAds() {
        List<Ads> allAds = adsRepository.findAll();
        if (allAds.isEmpty()) {
            return toAdsDtoList(adsRepository.findAll());
        }

        return toAdsDtoList(allAds);

    }

    @Override
    public AdsDto createAds(CreateAdsDto ads, AdsImage image) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUser(authentication.getName());
        Ads newAds = adsMapper.fromCreateAds(ads, user, image);
        Ads response = adsRepository.save(newAds);

        return adsMapper.toDto(response);
    }

    @Override
    public Collection<AdsDto> getAdsMe() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUser(authentication.getName());
        List<Ads> list = adsRepository.findAll();
        return toAdsDtoList(list.stream().filter(e -> e.getAuthor().equals(user)).collect(Collectors.toList()));
    }

    @Transactional
    @Override
    public AdsDto updateAds(long id, CreateAdsDto adsDto) {
        Ads ads = getAds(id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUser(authentication.getName());
        if(!ads.getAuthor().equals(user) && !userService.isAdmin(authentication)) {
            log.warn("Unavailable to update. It's not your ads! ads author = {}, username = {}", ads.getAuthor().getEmail(), user.getEmail());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unavailable to update. It's not your ads!");
        }

        Ads updatedAds = adsMapper.fromCreateAds(adsDto, user, ads.getImage());
        updatedAds.setAdsComments(List.copyOf(ads.getAdsComments()));
        updatedAds.setId(id);
        Ads saveAds = adsRepository.save(updatedAds);
        log.info("The ad with id = {} was updated ", id);

        return adsMapper.toDto(saveAds);

    }

    @Transactional
    @Override
    public Ads removeAds(long id) {
        Ads adsForRemove = adsRepository.findAdsById();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUser(authentication.getName());
        if (!adsForRemove.getAuthor().equals(user) && !userService.isAdmin(authentication)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Недоступно для удаления, это не ваше объявление");
        }
        adsRepository.deleteById(id);
        return  adsForRemove;
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
        Ads ads = getAds(adsId);
        List<AdsComment> commentList = adsCommentRepository.findAdsCommentByAdsId(ads);
        if (commentList.isEmpty()) {
        }
        return null;
    }
    @Override
    public AdsCommentDto createAdsComments(long id, AdsCommentDto adsCommentDto) {
        Ads ads = getAds(id);

        AdsComment comment = adsCommentMapper.toAdsComment (adsCommentDto);
        comment.setAdsId(ads);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails principalUser = (UserDetails) authentication.getPrincipal();
        User user = userService.getUser(principalUser.getUsername());

        comment.setAuthor(user);
        comment.setCreatedAt(OffsetDateTime.now());
        AdsComment savedComment = adsCommentRepository.save(comment);

        return adsCommentMapper.toDto(savedComment);
    }

    @Override
    public AdsDto deleteAdsComments(String adsId, long id) {
        AdsComment comment = getCommentsIfPresent(adsId,id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails principalUser = (UserDetails) authentication.getPrincipal();
        User user = userService.getUser(principalUser.getUsername());
        Ads ads = getAds(Integer.parseInt(adsId));

        if(!comment.getAuthor().equals(user) && !ads.getAuthor().equals(user) && !userService.isAdmin(authentication)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unavailable to update. It's not your comment!");

        }
        adsCommentRepository.deleteById(id);
        return null;

    }

    @Override
    public AdsCommentDto updateAdsComments(String adPk, long id, AdsCommentDto adsCommentDto) {
        Long adPkLg = Long.parseLong(adPk);
        Ads ads = getAds(adPkLg);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails principalUser = (UserDetails) authentication.getPrincipal();
        User user = userService.getUser(principalUser.getUsername());
        AdsComment comment = getCommentsIfPresent(adPk, id);

        if(!comment.getAuthor().equals(user) && !userService.isAdmin(authentication)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unavailable to update. It's not your comment!");
        }
        AdsComment comm = adsCommentMapper.toAdsComment(adsCommentDto);
        comm.setAuthor(user);
        comm.setAdsId(ads);
        comm.setId(id);
        comm.setCreatedAt(OffsetDateTime.now());
        adsCommentRepository.save(comm);
        return adsCommentMapper.toDto(comm);
    }

    private List<AdsDto> toAdsDtoList(List<Ads> ads) {
        return ads.stream()
                .map(adsMapper::toDto)
                .collect(Collectors.toList());
    }

    private AdsComment getCommentsIfPresent(String adsId, long id) {
        Long adPkInt = Long.parseLong(adsId);
        getAds(adPkInt);
        AdsComment comments = adsCommentRepository.findById(id).orElseThrow(() -> {
            return new NotFoundException("Комментарий с указанным идентификатором " + id + " does not exist");
        });
        return comments;
    }


}
