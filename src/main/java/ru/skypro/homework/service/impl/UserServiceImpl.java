package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Override
    public UserDto createUser(RegisterReq registerReq) {
        log.info("Trying to create and save new user");
        User newUser = userRepository.save(userMapper.toUser(registerReq));
        log.info("The user with id = {} was saved ", newUser.getId());
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
        log.info("Trying to update the userDto with username = {}", userDto.getLogin());
        checkUserDtoNeededFieldsIsNotNull(userDto);
        log.info("Trying to check that needed fields is not null ");
        User user = getUserFromAuthentication();

        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPhoneNumber(userDto.getPhoneNumber());
        User response = userRepository.save(user);
        log.info("The userDto with id = {} was updated ", response.getId());
        return userMapper.toDto(response);
    }

    @Transactional
    @Override
    public UserDto updateUserImage(MultipartFile file) {
        log.info("Trying to update image at the userDto");
        User user = getUserFromAuthentication();
        log.info("The userDto is found, updating...");
        if (userAvatarRepository.findUserAvatarByUser(user).isPresent()) {
            log.info("if avatar is found, delete it");
            userAvatarRepository.deleteUserAvatarByUser(user);
        }
        try {
            UserAvatar newUserAvatar = imageMapper.toUserAvatar(file);
            newUserAvatar.setUser(user);
            userAvatarRepository.save(newUserAvatar);
            log.info("Avatar was updated");
        } catch (IOException e) {
            log.warn("unable to save image");
            throw new SaveFileException();
                    }
        return userMapper.toDto(user);
    }

    @Transactional
    @Override
    public NewPasswordDto setPassword(NewPasswordDto newPasswordDto) {
        log.info("trying to set new password");
        if (newPasswordDto.getCurrentPassword().equals(newPasswordDto.getNewPassword())) {
             throw new PasswordsAreEqualsException();
        }
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        User user = getUserFromAuthentication();
        if (!encoder.matches(newPasswordDto.getCurrentPassword(), user.getPassword())) {
            log.debug("пароли не совпадают");
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
        log.info("try to remove if found by id");
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
        log.info("Try to check whether the login is used or not");
        userRepository.findUserByLoginIgnoreCase(login)
                .orElseThrow(UserNotFoundException::new);
        return true;
    }

    @Override
    public boolean isAdmin() {
        log.info("Try to check whether the user is an administrator or not");
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("admin_full_access"));
    }

    @Transactional
    @Override
    public User getUserFromAuthentication() {
        log.info("Try to get user from authentication");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return getUser(authentication.getName());
    }

    @Transactional
    @Override
    public byte[] getAvatar(UUID avatarUuid) {
        log.info("Try to find avatar by Uuid");
        return userAvatarRepository.findUserAvatarByAvatarUuid(avatarUuid)
                .map(UserAvatar::getData)
                .orElseThrow(UserAvatarNotFoundException::new);
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
