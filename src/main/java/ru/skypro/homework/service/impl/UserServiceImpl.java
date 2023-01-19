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
    private final ImageMapper imageMapper;

    @Value("userImage")
    private String userImageDir;

    /**
     * Метод создания пользователя, принимающий в параметры
     * @param registerReq ДТО с информацией о новом пользователе и
     * @return возвращающий ДТО с дабавленной в базу информацией
     */
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

    /**
     * Метод изменения пользователя.
     * @param userDto принимает ДТО с обновленной информацией.
     *                Изменяются только заполненные поля
     * @return возвращает ДТО с актуализированной информацией
     */
    @Transactional
    @Override
    public UserDto updateUser(UserDto userDto) {
        log.info("Trying to update the userDto with username = {}", userDto.getLogin());
        testUserDtoNeededFieldsIsNotNull(userDto);
        log.info("Trying to check that needed fields is not null ");
        User user = getUserFromAuthentication();

        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPhoneNumber(userDto.getPhoneNumber());
        User response = userRepository.save(user);
        log.info("The userDto with id = {} was updated ", response.getId());
        return userMapper.toDto(response);
    }

    /**
     * Метод изменения фото пользователя, принимающий в параметры
     * @param file
     * @return возвращает ДТО с актуализированной информацией
     */
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
            throw new SaveFileException();
        }
        return userMapper.toDto(user);
    }
    /**
     * Метод присваивания пароля.
     * @param newPasswordDto Принимает ДТО с текущим и новым паролем
     * @return новый пароль, либо ResponseStatusException
     */
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
            log.debug("указан неверный пароль");
            throw new PasswordsAreNotEqualsException();
        }

        String newPass = encoder.encode(newPasswordDto.getNewPassword());
        user.setPassword(newPass);
        User response = userRepository.save(user);
        log.info("The user with login = {} was updated ", response.getLogin());

        return newPasswordDto;

    }

    @Override//todo delete thismethod
    public UserDto removeUser(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
        userRepository.delete(user);
        return userMapper.toDto(user);
    }
    /**
     * Метод удаления пользователя, принимающий в параметры
     * @param id идентификатор пользователя и если пользователь найден
     * @return возвращает удаленного пользователя, либо исключение NotFoundException.
     */
    @Override//todo delete thismethod
    public UserDto getUserById(long id) {
        return userMapper.toDto(userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new));
    }
    /**
     * Мето получения пользователя по имени, принимающий в параметры
     * @param login имя пользователя и если найден,
     * @return возвращает пользователя, либо возвращает NotFoundException
     */
    @Transactional
    @Override
    public User getUser(String login) {
        log.info("Try to get user by user id");
        return userRepository.findUserByLoginIgnoreCase(login)
                .orElseThrow(UserNotFoundException::new);
    }

    /**
     * Метод проверки регистрации пользователя, принимающий в параметры
     * @param login логин и если такой логин уже существует,
     *              выбрасывает ошибку LoginAlreadyUsedException, а если отсутсвует
     * @return возвращает true
     */
    @Transactional
    @Override
    public boolean testUserForRegisterOk(String login) {
        log.info("Try to сheck check whether the login is used or not");
        if (userRepository.findUserByLoginIgnoreCase(login).isPresent()) {
            log.debug("this login is already used");
            throw new LoginAlreadyUsedException();
        }
        return true;
    }

    @Transactional
    @Override
    public Optional<User> userExists(String login) {
        log.info("Try to find existing user ");
        return userRepository.findUserByLoginIgnoreCase(login);
    }

    /**
     * Метод проверки является ли пользователь администратором
     * @return
     */
    @Override
    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
                && authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));
    }

    @Transactional
    @Override
    public User getUserFromAuthentication() {
        log.info("Try to get user from authentication");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return getUser(authentication.getName());
    }

    /**
     * Метод добаавления аватарки, принимающий в параметры
     * @param file картинку
     */
    @Override////todo delete thismethod
    public void uploadAvatar(MultipartFile file) {
        log.info("try to upload avatar");
        User user = getUserFromAuthentication();
        log.info("try to get user from authentication ");

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
            log.info("Awatar was uploaded");

        } catch (IOException e) {
            throw new SaveFileException();
        }
    }

    /**
     * Метод скачивания аватарки
     * @return
     */
    @Override//todo delete thismethod
    public byte[] downloadAvatar() {
        log.info("try to download avatar");
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
    public byte[] getAvatar(Long avatarId) {
        log.info("Try to find avatar by id");
        return userAvatarRepository.findUserAvatarByAvatarId(avatarId)
                .map(UserAvatar::getData)
                .orElseThrow(UserAvatarNotFoundException::new);
    }

    //todo delete thismethod
    private String getExtension(String originalFileName) {
        log.info("Try to get extension");
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

}
