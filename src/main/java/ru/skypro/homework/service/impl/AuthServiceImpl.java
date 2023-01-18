package ru.skypro.homework.service.impl;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.RegisterReq;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.service.AuthService;
import ru.skypro.homework.service.UserService;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserDetailsManager manager;

    private final PasswordEncoder encoder;

    private final UserService userService;


    public AuthServiceImpl(UserDetailsManager manager, UserService userService) {
        this.manager = manager;
        this.userService = userService;
        this.encoder = new BCryptPasswordEncoder();
    }

    @Override
    public boolean login(String login, String password) {
        if (!manager.userExists(login)) {
            return false;
        }
        UserDetails userDetails = manager.loadUserByUsername(login);
        String encryptedPassword = userDetails.getPassword();
        String encryptedPasswordWithoutEncryptionType = encryptedPassword.substring(8);
        return encoder.matches(password, encryptedPasswordWithoutEncryptionType);
    }

    @Override
    public boolean register(RegisterReq registerReq, Role role) {
        if (manager.userExists(registerReq.getLogin())) {
            return false;
        }

        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        manager.createUser(
                User.withUsername(registerReq.getLogin())
                        .password(encoder.encode(registerReq.getPassword()))
                        .roles(role.name())
                        .build()
        );
        if (userService.testUserForRegisterOk(registerReq.getLogin())) {
            registerReq.setRole(role);
            registerReq.setPassword(encoder.encode(registerReq.getPassword()));
            userService.createUser(registerReq);
        }
        return true;
    }

}
