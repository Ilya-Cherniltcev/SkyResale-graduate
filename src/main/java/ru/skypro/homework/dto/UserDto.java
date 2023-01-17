package ru.skypro.homework.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class UserDto {
    @JsonProperty("id")
    private long userId; // * auto increasing *
    @JsonProperty("email")
    private String login;
    private String firstName;
    private String lastName;
    @JsonProperty("phone")
    private String phoneNumber;
    private String regDate;
    private String city;
    private String image;


}
