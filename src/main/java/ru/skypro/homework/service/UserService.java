package ru.skypro.homework.service;

import org.springframework.security.core.Authentication;
import ru.skypro.homework.dto.NewPasswordDto;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.model.User;

import java.util.Collection;

public interface UserService {
    UserDto createUser(UserDto userDto);

    Collection<UserDto> getUsers();

    UserDto updateUser(UserDto userDto);

    NewPasswordDto setPassword(NewPasswordDto newPassword);

    User removeUser(long id);

    User getUserById(long id);

    User getUser (String username);
    boolean isAdmin(Authentication authentication);
    }
