package ru.skypro.homework.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Validated
@Data
public class RegisterReq {
    @Email (regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}" ,
            message = "Email должен быть корректным адресом электронной почты")
    private String login;
    private String password;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @Pattern(regexp = "\\+7[0-9]{10}",
            message = "Телефонный номер должен начинаться с +7, затем - 10 цифр")
    @JsonProperty("phone")
    private String phoneNumber;
    private Role role;
}
