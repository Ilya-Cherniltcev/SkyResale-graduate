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
import ru.skypro.homework.repository.UserAvatarRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.UserService;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserAvatarRepository userAvatarRepository;
    private final UserMapper userMapper;
    private final ImageMapper imageMapper;

    @Value("userImage")
    private String userImageDir;

    /**
     * Create new {@link User} in {@link UserRepository}
     * @param registerReq data from register form
     * @return new UserDto
     */
    @Override
    public UserDto createUser(RegisterReq registerReq) {
        User newUser = userRepository.save(userMapper.toUser(registerReq));
        return userMapper.toDto(newUser);
    }

    /**
     * Get user data for current {@link User} from {@link UserRepository}
     * @return needed data
     */
    @Transactional
    @Override
    public UserDto getUserMe() {
        return userMapper.toDto(getUserFromAuthentication());
    }

    /**
     * Update {@link User} data in {@link UserRepository}
     * @param userDto needed data for update User, must be {@link NotNull}
     * @return Updated UserDto
     * @throws NoContentException if one of fields is {@link Null}
     */
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

    /**
     * Update current or create new {@link UserAvatar} in {@link UserAvatarRepository}
     * @param file uploaded file
     * @return Updated UserDto
     * @throws SaveFileException if something went wrong, when image updated
     */
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

    /**
     *Change current password of current {@link User} in {@link UserRepository}
     * @param newPasswordDto needed passwords from input form
     * @return updated password
     * @throws PasswordsAreEqualsException if current password and new password are same
     * @throws PasswordsAreNotEqualsException if written current password is not equal of password current User
     */
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

    /**
     * Delete {@link User} by Id from {@link UserRepository}
     * <br> ONLY FOR ADMIN
     * @param id Id of User
     * @return UserDto of removed User
     * @throws UserNotFoundException if this User not exist in database
     */
    @Override
    public UserDto removeUser(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
        userRepository.delete(user);
        return userMapper.toDto(user);
    }

    /**
     * Find {@link User} by login (ignore case) in database via method {@link UserRepository}
     * @param login of needed User
     * @return Founded User
     * @throws UserNotFoundException if User not found in database
     */
    @Transactional
    @Override
    public User getUser(String login) {
        return userRepository.findUserByLoginIgnoreCase(login)
                .orElseThrow(UserNotFoundException::new);
    }

    /**
     * Check login before create new {@link User}
     * @param login which was written in register form
     * @return true if all is OK
     * @throws LoginAlreadyUsedException if this login already used
     */
    @Transactional
    @Override
    public boolean checkUserForRegisterOk(String login) {
        if (userRepository.findUserByLoginIgnoreCase(login).isPresent()) {
            throw new LoginAlreadyUsedException();
        }
        return true;
    }

    /**
     * Check {@link User} with needed login is existed
     * @param login needed login
     * @return true if user existed
     * @throws UserNotFoundException – if needed User not found in database
     */
    @Transactional
    @Override
    public boolean checkUserExists(String login) {
        userRepository.findUserByLoginIgnoreCase(login)
                .orElseThrow(UserNotFoundException::new);
        return true;
    }

    /**
     * Check current User is Admin
     * @return true if User is Admin
     */
    @Override
    public boolean isAdmin() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("admin_full_access"));
    }

    /**
     * Get User from Authentication
     * @return User
     */
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

    /**
     * Get array of byte of {@link UserAvatar} from {@link UserAvatarRepository}
     * @param avatarId Id in database
     * @return Avatar
     * @throws UserAvatarNotFoundException if Avatar not found in database
     */
    @Transactional
    @Override
    public byte[] getAvatar(Long avatarId) {
        return userAvatarRepository.findUserAvatarByAvatarId(avatarId)
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
