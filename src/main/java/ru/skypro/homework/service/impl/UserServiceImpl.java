package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.exception.*;
import ru.skypro.homework.mapper.ImageMapper;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.model.User;
import ru.skypro.homework.model.UserAvatar;
import ru.skypro.homework.repository.AdsRepository;
import ru.skypro.homework.repository.UserAvatarRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.UserService;

import javax.validation.constraints.Null;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AdsRepository adsRepository;
    private final UserAvatarRepository userAvatarRepository;
    private final UserMapper userMapper;
    private final ImageMapper imageMapper;

    @Value("userImage")
    private String userImageDir;


    @Override
    public UserDto createUser(RegisterReq registerReq) {
        User newUser = userRepository.save(userMapper.toUser(registerReq));
        return userMapper.toDto(newUser);
    }

    @Transactional
    @Override
    public UserDto getUserMe() {
        return userMapper.toDto(getUserFromAuthentication());
    }

    @Transactional
    @Override
    public UserDto updateUser(UserDto userDto) {
        checkUserDtoNeededFieldsIsNotNull(userDto);
        User user = getUserFromAuthentication();

        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPhoneNumber(userDto.getPhoneNumber());
        User response = userRepository.save(user);
        return userMapper.toDto(response);
    }

    @Transactional
    @Override
    public UserDto updateUserImage(MultipartFile file) {
        User user = getUserFromAuthentication();
        if (userAvatarRepository.findUserAvatarByUser(user).isPresent()) {
            userAvatarRepository.deleteUserAvatarByUser(user);
        }
        try {
            UserAvatar newUserAvatar = imageMapper.toUserAvatar(file);
            newUserAvatar.setUser(user);
            userAvatarRepository.save(newUserAvatar);
        } catch (IOException e) {
            throw new SaveFileException();
        }
        return userMapper.toDto(user);
    }

    @Transactional
    @Override
    public NewPasswordDto setPassword(NewPasswordDto newPasswordDto) {
        if (newPasswordDto.getCurrentPassword().equals(newPasswordDto.getNewPassword())) {
            throw new PasswordsAreEqualsException();
        }
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        User user = getUserFromAuthentication();
        if (!encoder.matches(newPasswordDto.getCurrentPassword(), user.getPassword())) {
            throw new PasswordsAreNotEqualsException();
        }

        String newPass = encoder.encode(newPasswordDto.getNewPassword());
        user.setPassword(newPass);
        User response = userRepository.save(user);
        log.info("The user with login = {} was updated ", response.getLogin());

        return newPasswordDto;

    }

    @Transactional
    @Override
    public UserDto removeUser(long id) {
        User userForDelete = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
        adsRepository.deleteAllByAuthor(userForDelete);
        userRepository.delete(userForDelete);
        return userMapper.toDto(userForDelete);
    }

    @Transactional
    @Override
    public User getUser(String login) {
        return userRepository.findUserByLoginIgnoreCase(login)
                .orElseThrow(UserNotFoundException::new);
    }

    @Transactional
    @Override
    public boolean checkUserForRegisterOk(String login) {
        if (userRepository.findUserByLoginIgnoreCase(login).isPresent()) {
            throw new LoginAlreadyUsedException();
        }
        return true;
    }

    @Transactional
    @Override
    public boolean checkUserExists(String login) {
        userRepository.findUserByLoginIgnoreCase(login)
                .orElseThrow(UserNotFoundException::new);
        return true;
    }

    @Override
    public boolean isAdmin() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("admin_full_access"));
    }

    @Transactional
    @Override
    public User getUserFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return getUser(authentication.getName());
    }

    @Override////todo delete thismethod
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

    @Override//todo delete thismethod
    public byte[] downloadAvatar() {
        UserAvatar avatar = userAvatarRepository.findUserAvatarByUser(getUserFromAuthentication())
                .orElseThrow(UserAvatarNotFoundException::new);
        try {
            return Files.readAllBytes(Paths.get(avatar.getFilePath()));
        } catch (IOException e) {
            throw new ReadFileException();
        }
    }

    @Transactional
    @Override
    public byte[] getAvatar(UUID avatarUuid) {
        return userAvatarRepository.findUserAvatarByAvatarUuid(avatarUuid)
                .map(UserAvatar::getData)
                .orElseThrow(UserAvatarNotFoundException::new);
    }

    //todo delete thismethod
    private String getExtension(String originalFileName) {
        String extension = StringUtils.substringAfter(originalFileName, ".");
        if (!originalFileName.contains(".") || extension.isBlank() || extension.contains(".")) {
            throw new RuntimeException();
        }
        return extension;
    }

    /**
     * Check fields of {@link UserDto} on {@code null}
     * @param userDto Dto for update User
     * @throws NoContentException if one of fields is {@link Null}
     */
    private void checkUserDtoNeededFieldsIsNotNull(UserDto userDto) {
        if (userDto.getFirstName() == null || userDto.getLastName() == null || userDto.getPhoneNumber() == null) {
            throw new NoContentException();
        }
    }

}
