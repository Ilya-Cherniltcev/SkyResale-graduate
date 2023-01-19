package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.homework.dto.RegisterReq;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.model.User;
import ru.skypro.homework.model.UserAvatar;

@Mapper
public interface UserMapper {

    @Mapping(target = "userId", source = "id")
    @Mapping(target = "image", expression = "java(getImageLink(user.getUserAvatar()))")
    UserDto toDto(User user);

    @Mapping(target = "phoneNumber", source = "registerReq.phone")
    @Mapping(target = "role", source = "registerReq.role")
    User toUser(RegisterReq registerReq);

    @Transactional
    default String getImageLink(UserAvatar userAvatar) {
        if (userAvatar == null) {
            return null;
        }
        return "/users/image/" + userAvatar.getAvatarId();
    }
}
