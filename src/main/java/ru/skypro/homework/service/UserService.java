package ru.skypro.homework.service;

import org.springframework.security.core.Authentication;
import ru.skypro.homework.dto.CreateUserDto;
import ru.skypro.homework.dto.NewPasswordDto;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.model.User;

public interface UserService {
    UserDto createUser(CreateUserDto userDto);

    UserDto getUserMe();

    UserDto updateUser(UserDto userDto);

    NewPasswordDto setPassword(NewPasswordDto newPassword);

    UserDto removeUser(long id);

    UserDto getUserById(long id);

    User getUser (String username);
    boolean isAdmin(Authentication authentication);
    }
