package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.model.User;

@Mapper
public interface UserMapper {

    @Mapping(source = "id", target = "userId")
    UserDto toDto(User user);

    User toAds(UserDto userDto);
}
