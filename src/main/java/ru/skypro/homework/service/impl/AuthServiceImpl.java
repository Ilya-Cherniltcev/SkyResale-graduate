package ru.skypro.homework.service.impl;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private final UserService userService;
    private final PasswordEncoder encoder;

    public AuthServiceImpl(UserDetailsManager manager, UserService userService) {
        this.manager = manager;
        this.userService = userService;
        this.encoder = new BCryptPasswordEncoder();
    }

    @Override
    public boolean login(String login, String password) {
        if (userService.userExists(login).isEmpty()) {
            return false;
        }
        ru.skypro.homework.model.User user = userService.getUser(login);
        String usersPassword = user.getPassword();
        return encoder.matches(password, usersPassword);
    }

    @Override
    public boolean register(RegisterReq registerReq, Role role) {
        if (manager.userExists(registerReq.getLogin())) {
            return false;
        }
        String encodedPassword = encoder.encode(registerReq.getPassword());
        registerReq.setPassword(encodedPassword);
        registerReq.setRole(role);
        userService.createUser(registerReq);
        return true;
    }
}
