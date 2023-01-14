package ru.skypro.homework.dto;

import lombok.Data;


@Data
public class UserDto {
    private long userId; // * auto increasing *
    private String login;
    private String password;
    private String firstName;
    private String lastName;
    private String phoneNumber;



}
