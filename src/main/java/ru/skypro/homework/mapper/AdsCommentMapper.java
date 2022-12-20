package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.skypro.homework.dto.AdsCommentDto;
import ru.skypro.homework.model.AdsComment;

@Mapper
public interface AdsCommentMapper {
    @Mapping(source = "id", target = "adsCommentId")
    AdsCommentDto toDto(AdsComment adsComment);

    AdsComment toAdsComment(AdsCommentDto adsCommentDto);
}
