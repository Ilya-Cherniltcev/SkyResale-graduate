package ru.skypro.homework.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.skypro.homework.controller.UserController;
import ru.skypro.homework.dto.CreateUserDto;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.UserService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.skypro.homework.ConstantsForTests.DTO_IMAGE_ARRAY;

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

    @BeforeEach
    void setup() throws Exception {
        user.setId(1L);
        user.setLogin("user@gmail.com");
        user.setPassword("password");
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

    @Test
    void createUser() throws Exception {
        when(userRepository.save(userMapper.toUser(createUserDto)))
                .thenReturn(user);

        JSONObject userObject = new JSONObject();
        userObject.put("login", createUserDto.getLogin());
        userObject.put("password", createUserDto.getPassword());
        userObject.put("firstName", createUserDto.getFirstName());
        userObject.put("lastName", createUserDto.getLastName());
        userObject.put("phoneNumber", createUserDto.getPhoneNumber());
        System.out.println("=====================================================================");
        System.out.println(userObject);
        System.out.println("=====================================================================");
        when(userRepository.save(user)).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .content(userObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value(createUserDto.getLogin()))
                .andExpect(jsonPath("$.password").value(createUserDto.getPassword()))
                .andExpect(jsonPath("$.firstName").value(createUserDto.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(createUserDto.getLastName()))
                .andExpect(jsonPath("$.phoneNumber").value(createUserDto.getPhoneNumber()));

    }

}
