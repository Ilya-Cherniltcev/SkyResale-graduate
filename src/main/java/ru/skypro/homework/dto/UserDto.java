package ru.skypro.homework.dto;

import lombok.Data;

@Data
public class UserDto {
    private long userId; // * auto increasing *
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
}
