package ru.skypro.homework.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class UserDto {
    private long userId; // * auto increasing *
    private String login;
    private String password;
    private String firstName;
    private String lastName;
    @JsonProperty("phone")
    private String phoneNumber;



}
