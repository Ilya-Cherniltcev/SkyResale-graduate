package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPasswordDto;
import ru.skypro.homework.dto.RegisterReq;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.exception.*;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.model.User;
import ru.skypro.homework.model.UserAvatar;
import ru.skypro.homework.repository.UserAvatarRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.UserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserAvatarRepository userAvatarRepository;
    private final UserMapper userMapper;

    @Value("userImage")
    private String userImageDir;

    @Override
    public void createUser(RegisterReq registerReqDto) {
        User user = userMapper.registerReqDtoToUser(registerReqDto);
        userRepository.save(user);
    }


    @Override
    public UserDto getUserMe() {
        return userMapper.toDto(getUserFromAuthentication());
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        testUserDtoNeededFieldsIsNotNull(userDto);
        User user = getUserFromAuthentication();

        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPhoneNumber(userDto.getPhoneNumber());
        User response = userRepository.save(user);
        return userMapper.toDto(response);
    }

    @Override
    public String updateUserImage(MultipartFile file) {
        return null;
    }

    @Override
    public NewPasswordDto setPassword(NewPasswordDto newPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User user = getUserFromAuthentication();
        if (!passwordEncoder.matches(newPassword.getCurrentPassword(), user.getPassword())) {
            throw new PasswordException();
        }

        String newPass = passwordEncoder.encode(newPassword.getNewPassword());
        user.setPassword(newPass);
        User response = userRepository.save(user);
        log.info("The user with login = {} was updated ", response.getLogin());

        return newPassword;

    }

    @Override
    public UserDto removeUser(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
        userRepository.delete(user);
        return userMapper.toDto(user);
    }

    @Override
    public UserDto getUserById(long id) {
        return userMapper.toDto(userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new));
    }

    @Override
    public User getUser(String login) {
        return userRepository.findUserByLoginIgnoreCase(login)
                .orElseThrow(UserNotFoundException::new);
    }

    @Override
    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
                && authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));
    }

    @Override
    public User getUserFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return getUser(authentication.getName());
    }

    @Override//not ready
    public void uploadAvatar(MultipartFile file) {
        User user = getUserFromAuthentication();
        try {
            Path filePath = Path.of(userImageDir, user.getId() + "." + getExtension(file.getOriginalFilename()));
            Files.createDirectories((filePath.getParent()));
            Files.deleteIfExists(filePath);
            Files.write(filePath, file.getBytes());

            UserAvatar userAvatar = userAvatarRepository.findUserAvatarByUser(user)
                    .orElse(new UserAvatar());
            userAvatar.setFilePath(filePath.toString());
            userAvatar.setFilesize(file.getSize());
            userAvatar.setMediaType(file.getContentType());
            userAvatar.setData(file.getBytes());
            userAvatar.setUser(user);
            userAvatarRepository.save(userAvatar);
        } catch (IOException e) {
            throw new SaveFileException();
        }
    }

    @Override
    public byte[] downloadAvatar() {
        UserAvatar avatar = userAvatarRepository.findUserAvatarByUser(getUserFromAuthentication())
                .orElseThrow(UserAvatarNotFoundException::new);
        try {
            return Files.readAllBytes(Paths.get(avatar.getFilePath()));
        } catch (IOException e) {
            throw new ReadFileException();
        }
    }

    private String getExtension(String originalFileName) {
        String extension = StringUtils.substringAfter(originalFileName, ".");
        if (!originalFileName.contains(".") || extension.isBlank() || extension.contains(".")) {
            throw new RuntimeException();
        }
        return extension;
    }

    private void testUserDtoNeededFieldsIsNotNull(UserDto userDto) {
        if (userDto.getFirstName() == null || userDto.getLastName() == null || userDto.getPhoneNumber() == null) {
            throw new NoContentException();
        }
    }
    @Override
    public Optional<User> userExists(String login) {
        return userRepository.findUserByLoginIgnoreCase(login);
    }
}
