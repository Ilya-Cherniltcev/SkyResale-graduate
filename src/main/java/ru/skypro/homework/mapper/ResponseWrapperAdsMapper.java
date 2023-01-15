package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.ResponseWrapperAdsDto;

import java.util.List;

@Mapper
public interface ResponseWrapperAdsMapper {

    ResponseWrapperAdsDto toResponseWrapperAdsDto(Integer count, List<AdsDto> results);
}
