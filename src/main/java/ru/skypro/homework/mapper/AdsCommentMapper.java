package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.skypro.homework.dto.AdsCommentDto;
import ru.skypro.homework.dto.CreateAdsCommentDto;
import ru.skypro.homework.model.AdsComment;

@Mapper
public interface AdsCommentMapper {
    @Mapping(target = "author", source = "adsComment.author.id")
    @Mapping(target = "ads", source = "adsComment.adsId.id")
    AdsCommentDto toDto(AdsComment adsComment);
    @Mapping(target = "author.id", source = "author")
    AdsComment toAdsComment(AdsCommentDto adsCommentDto);
    CreateAdsCommentDto createToDto(AdsComment adsComment);
    AdsComment createToAdsComment(CreateAdsCommentDto adsCommentDto);

}
