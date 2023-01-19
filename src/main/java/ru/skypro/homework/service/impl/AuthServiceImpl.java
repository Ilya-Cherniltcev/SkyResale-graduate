package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.homework.dto.RegisterReq;
import ru.skypro.homework.exception.LoginAlreadyUsedException;
import ru.skypro.homework.exception.UserNotFoundException;
import ru.skypro.homework.model.Role;
import ru.skypro.homework.service.AuthService;
import ru.skypro.homework.service.UserService;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager manager;

    private final PasswordEncoder encoder;

    private final UserService userService;

    /**
     * Authenticate User in signin form
     *
     * @param username login for authentication
     * @param password password for authentication
     * @return true if User authenticated
     * @throws UserNotFoundException – if needed User not found in database
     */
    @Transactional
    @Override
    public boolean login(String username, String password) {
        userService.checkUserExists(username);

        Authentication authentication = manager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication.isAuthenticated();
    }

    /**
     * Check login for uniqueness, if login is unique create new {@link ru.skypro.homework.model.User} in {@link ru.skypro.homework.repository.UserRepository}
     *
     * @param registerReq Dto from registration form
     * @param role        role of this User
     * @return true if new User created and added in database
     * @throws LoginAlreadyUsedException if User with this login already existed
     */
    @Override
    public boolean register(RegisterReq registerReq, Role role) {
        if (userService.checkUserForRegisterOk(registerReq.getLogin())) {
            registerReq.setRole(role);
            registerReq.setPassword(encoder.encode(registerReq.getPassword()));
            userService.createUser(registerReq);
        }
        return true;
    }

}
