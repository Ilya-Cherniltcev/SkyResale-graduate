package ru.skypro.homework.dto;

import lombok.Data;

@Data
public class RegisterReq {
    private String login;
    private String password;
    private String firstName;
    private String lastName;
    private String phone;
    private Role role;
}
