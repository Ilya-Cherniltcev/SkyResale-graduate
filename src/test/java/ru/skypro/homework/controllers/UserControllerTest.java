package ru.skypro.homework.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.skypro.homework.controller.UserController;
import ru.skypro.homework.dto.CreateUserDto;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.UserService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.skypro.homework.ConstantsForTests.*;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    UserMapper userMapper;

    @MockBean
    private UserRepository userRepository;

    private final User user = new User();
    private final CreateUserDto createUserDto = new CreateUserDto();

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() throws Exception {
        user.setId(1L);
        user.setLogin(DEFAULT_LOGIN);
        user.setPassword(DEFAULT_PASSWORD);
        user.setFirstName("user1");
        user.setLastName("lastname1");
        user.setPhoneNumber("+7901");
        user.setRole("USER");
        createUserDto.setLogin(user.getLogin());
        createUserDto.setPassword(user.getPassword());
        createUserDto.setFirstName(user.getFirstName());
        createUserDto.setLastName(user.getLastName());
        createUserDto.setPhoneNumber(user.getPhoneNumber());
    }

    @Disabled
    void createUser() throws Exception {
        when(userRepository.save(userMapper.toUser(createUserDto)))
                .thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
//                        .content(userObject.toString())

                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value(createUserDto.getLogin()))
                .andExpect(jsonPath("$.password").value(createUserDto.getPassword()))
                .andExpect(jsonPath("$.firstName").value(createUserDto.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(createUserDto.getLastName()))
                .andExpect(jsonPath("$.phoneNumber").value(createUserDto.getPhoneNumber()));
    }

    @Test
    void updateUser() throws Exception {
        when(userRepository.save(userMapper.toUser(createUserDto)))
                .thenReturn(user);

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        System.out.println(objectMapper.writeValueAsString(user));
        System.out.println("****************************************");
        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/users/me")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(httpBasic(DEFAULT_LOGIN,DEFAULT_PASSWORD))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value(user.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(user.getLastName()))
                .andExpect(jsonPath("$.phoneNumber").value(user.getPhoneNumber()));



    }
}
