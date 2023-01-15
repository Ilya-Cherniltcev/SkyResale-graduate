package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import ru.skypro.homework.dto.AdsCommentDto;
import ru.skypro.homework.dto.ResponseWrapperCommentDto;

import java.util.List;

@Mapper
public interface ResponseWrapperCommentMapper {

    ResponseWrapperCommentDto toResponseWrapperCommentDto(Integer count, List<AdsCommentDto> results);
}
