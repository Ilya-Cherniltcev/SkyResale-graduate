package ru.skypro.homework.service;

import ru.skypro.homework.dto.RegisterReq;
import ru.skypro.homework.exception.LoginAlreadyUsedException;
import ru.skypro.homework.exception.UserNotFoundException;
import ru.skypro.homework.model.Role;

public interface AuthService {

    /**
     * Authenticate User in signin form
     *
     * @param login login for authentication
     * @param password password for authentication
     * @return true if User authenticated
     * @throws UserNotFoundException – if needed User not found in database
     */
    boolean login(String login, String password);

    /**
     * Check login for uniqueness, if login is unique create new {@link ru.skypro.homework.model.User} in {@link ru.skypro.homework.repository.UserRepository}
     *
     * @param registerReq Dto from registration form
     * @param role        role of this User
     * @return true if new User created and added in database
     * @throws LoginAlreadyUsedException if User with this login already existed
     */
    boolean register(RegisterReq registerReq, Role role);
}
