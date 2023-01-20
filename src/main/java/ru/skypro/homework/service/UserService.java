package ru.skypro.homework.service;

import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.model.User;

import java.util.UUID;

public interface UserService {
    UserDto createUser(RegisterReq registerReq);

    UserDto getUserMe();

    UserDto updateUser(UserDto userDto);

    UserDto updateUserImage(MultipartFile file);

    NewPasswordDto setPassword(NewPasswordDto newPassword);

    UserDto removeUser(long id);

    User getUser(String login);

    boolean checkUserForRegisterOk(String login);

    boolean isAdmin();

    User getUserFromAuthentication();

    void uploadAvatar(MultipartFile file);

    byte[] downloadAvatar();

    byte[] getAvatar(UUID avatarUuid);

    boolean checkUserExists(String login);
}
