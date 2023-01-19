package ru.skypro.homework.service;

import ru.skypro.homework.dto.RegisterReq;
import ru.skypro.homework.model.Role;

public interface AuthService {
    boolean login(String login, String password);
    boolean register(RegisterReq registerReq, Role role);

}
