package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.skypro.homework.dto.CreateUserDto;
import ru.skypro.homework.dto.RegisterReq;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.model.User;

@Mapper
public interface UserMapper {

    @Mapping(source = "id", target = "userId")
    UserDto toDto(User user);
    @Mapping(target = "role", expression = "java(setRole())")
//    @Mapping(target = "login", expression = "java(setLogin())")
    User toUser (CreateUserDto userDto);
    @Mapping(target = "login", source = "login")
    User registerReqDtoToUser(RegisterReq registerReq);
    default String setRole() {
        return "USER";
    }
}
