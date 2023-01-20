package ru.skypro.homework.service;

import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.exception.*;
import ru.skypro.homework.model.User;
import ru.skypro.homework.model.UserAvatar;
import ru.skypro.homework.repository.UserAvatarRepository;
import ru.skypro.homework.repository.UserRepository;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.UUID;

public interface UserService {

    /**
     * Create new {@link User} in {@link UserRepository}
     *
     * @param registerReq data from register form
     * @return new UserDto
     */
    UserDto createUser(RegisterReq registerReq);

    /**
     * Get user data for current {@link User} from {@link UserRepository}
     *
     * @return needed data
     */
    UserDto getUserMe();

    /**
     * Update {@link User} data in {@link UserRepository}
     *
     * @param userDto needed data for update User, must be {@link NotNull}
     * @return Updated UserDto
     * @throws NoContentException if one of fields is {@link Null}
     */
    UserDto updateUser(UserDto userDto);

    /**
     * Update current or create new {@link UserAvatar} in {@link UserAvatarRepository}
     *
     * @param file uploaded file
     * @return Updated UserDto
     * @throws SaveFileException if something went wrong, when image updated
     */
    UserDto updateUserImage(MultipartFile file);

    /**
     * Change current password of current {@link User} in {@link UserRepository}
     *
     * @param newPasswordDto needed passwords from input form
     * @return updated password
     * @throws PasswordsAreEqualsException    if current password and new password are same
     * @throws PasswordsAreNotEqualsException if written current password is not equal of password current User
     */
    NewPasswordDto setPassword(NewPasswordDto newPasswordDto);

    /**
     * Delete {@link User} by Id from {@link UserRepository}
     * <br> ONLY FOR ADMIN
     *
     * @param id Id of User
     * @return UserDto of removed User
     * @throws UserNotFoundException if this User not exist in database
     */
    UserDto removeUser(long id);

    /**
     * Find {@link User} by login (ignore case) in database via method {@link UserRepository}
     *
     * @param login of needed User
     * @return Founded User
     * @throws UserNotFoundException if User not found in database
     */
    User getUser(String login);

    /**
     * Check login before create new {@link User}
     *
     * @param login which was written in register form
     * @return true if all is OK
     * @throws LoginAlreadyUsedException if this login already used
     */
    boolean checkUserForRegisterOk(String login);

    /**
     * Check {@link User} with needed login is existed
     *
     * @param login needed login
     * @return true if user existed
     * @throws UserNotFoundException – if needed User not found in database
     */
    boolean checkUserExists(String login);

    /**
     * Check current User is Admin
     *
     * @return true if User is Admin
     */
    boolean isAdmin();

    /**
     * Get User from Authentication
     *
     * @return User
     */
    User getUserFromAuthentication();

    void uploadAvatar(MultipartFile file);

    byte[] downloadAvatar();

    /**
     * Get array of byte of {@link UserAvatar} from {@link UserAvatarRepository}
     *
     * @param avatarUuid Id in database
     * @return Avatar
     * @throws UserAvatarNotFoundException if Avatar not found in database
     */
    byte[] getAvatar(UUID avatarUuid);
}
