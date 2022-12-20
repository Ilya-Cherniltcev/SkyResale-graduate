package ru.skypro.homework.dto;

import lombok.Data;

@Data
public class UserDto {
    private long userId; // * auto increasing *
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
}
