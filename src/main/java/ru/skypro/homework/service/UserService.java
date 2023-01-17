package ru.skypro.homework.service;

import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.model.User;

import java.util.Optional;

public interface UserService {
    UserDto createUser(RegisterReq registerReq);

    UserDto getUserMe();

    UserDto updateUser(UserDto userDto);

    UserDto updateUserImage(MultipartFile file);

    NewPasswordDto setPassword(NewPasswordDto newPassword);

    UserDto removeUser(long id);

    UserDto getUserById(long id);

    User getUser(String login);

    boolean testUserForRegisterOk(String login);

    boolean isAdmin();

    User getUserFromAuthentication();

    void uploadAvatar(MultipartFile file);

    byte[] downloadAvatar();

    byte[] getAvatar(Long avatarId);

    Optional<User> userExists(String login);
}
