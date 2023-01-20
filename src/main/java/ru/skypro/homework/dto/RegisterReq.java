package ru.skypro.homework.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.skypro.homework.model.Role;

import javax.validation.constraints.*;

@Data
public class RegisterReq {
    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}" ,
            message = "Email должен быть корректным адресом электронной почты")
    @JsonProperty("username")
    private String login;
    private String password;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @Pattern(regexp = "\\+7[0-9]{10}",
            message = "Телефонный номер должен начинаться с +7, затем - 10 цифр")
    private String phone;
    private Role role;
}
