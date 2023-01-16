package ru.skypro.homework.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LoginReq {
    @JsonProperty("username")
    private String login;
    private String password;


}
