package ru.skypro.homework.service;

import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.CreateUserDto;
import ru.skypro.homework.dto.NewPasswordDto;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.model.User;

public interface UserService {
    UserDto createUser(CreateUserDto userDto);

    UserDto getUserMe();

    UserDto updateUser(UserDto userDto);

    String updateUserImage(MultipartFile file);

    NewPasswordDto setPassword(NewPasswordDto newPassword);

    UserDto removeUser(long id);

    UserDto getUserById(long id);

    User getUser (String username);
    boolean isAdmin();
    User getUserFromAuthentication();

    void uploadAvatar(MultipartFile file);

    byte[] downloadAvatar();
}
