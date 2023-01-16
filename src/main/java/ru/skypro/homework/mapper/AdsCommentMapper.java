package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.skypro.homework.dto.AdsCommentDto;
import ru.skypro.homework.model.AdsComment;

@Mapper
public interface AdsCommentMapper {
    @Mapping(target = "author", source = "adsComment.author.id")
    AdsCommentDto toDto(AdsComment adsComment);
    @Mapping(target = "author",ignore = true)
    @Mapping(target = "id",ignore = true)
    @Mapping(target = "createdAt",ignore = true)
    AdsComment toAdsComment(AdsCommentDto adsCommentDto);

}
