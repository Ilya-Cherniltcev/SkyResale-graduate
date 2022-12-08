package ru.skypro.homework.service;

import ru.skypro.homework.dto.UserDto;

import java.util.Collection;

public interface UserService {
    UserDto createUser(UserDto userDto);

    Collection<UserDto> getUsers();

    UserDto updateUser(UserDto userDto);

    UserDto setPassword(String newPassword);

    UserDto removeUser(long id);

    UserDto getUserById(long id);
}
