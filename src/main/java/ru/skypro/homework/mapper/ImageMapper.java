package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.model.AdsImage;
import ru.skypro.homework.model.UserAvatar;

import java.io.IOException;

@Mapper
public interface ImageMapper {
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "filesize", source = "file.size")
    @Mapping(target = "mediaType", source = "file.contentType")
    @Mapping(target = "data", source = "file.bytes")
    AdsImage toAdsImage(MultipartFile file) throws IOException;

    @Mapping(target = "avatarUuid", ignore = true)
    @Mapping(target = "filesize", source = "file.size")
    @Mapping(target = "mediaType", source = "file.contentType")
    @Mapping(target = "data", source = "file.bytes")
    UserAvatar toUserAvatar(MultipartFile file) throws IOException;


}
