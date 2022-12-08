package ru.skypro.homework.service.impl;

import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.service.UserService;

import java.util.Collection;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public UserDto createUser(UserDto userDto) {
        return null;
    }

    @Override
    public Collection<UserDto> getUsers() {
        return null;
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        return null;
    }

    @Override
    public UserDto setPassword(String newPassword) {
        return null;
    }

    @Override
    public UserDto removeUser(long id) {
        return null;
    }

    @Override
    public UserDto getUserById(long id) {
        return null;
    }
}
