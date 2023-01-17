package ru.skypro.homework.service;

import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPasswordDto;
import ru.skypro.homework.dto.RegisterReq;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.model.User;

import java.util.Optional;

public interface UserService {

    void createUser(RegisterReq registerReqDto);

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

    Optional<User> userExists(String login);
}
