package ru.skypro.homework.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.skypro.homework.controller.AuthController;
import ru.skypro.homework.dto.LoginReq;
import ru.skypro.homework.dto.RegisterReq;
import ru.skypro.homework.model.Role;
import ru.skypro.homework.service.AuthService;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.skypro.homework.ConstantsForTests.*;

@WebMvcTest(AuthController.class)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {
    LoginReq loginReqDto = new LoginReq();
    RegisterReq registerReqDto = new RegisterReq();
    private final MockMvc mockMvc;
    ObjectMapper objectMapper;
    @MockBean
    private AuthService authService;

    @Autowired
    AuthControllerTest(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
    void setUp() {
        loginReqDto.setLogin(DEFAULT_LOGIN);
        loginReqDto.setPassword(ENCRYPTED_PASSWORD);

        registerReqDto.setLogin(DEFAULT_LOGIN);
        registerReqDto.setFirstName(DEFAULT_USERNAME);
        registerReqDto.setLastName(DEFAULT_LASTNAME);
        registerReqDto.setPassword(DEFAULT_PASSWORD);
        registerReqDto.setPhone(DEFAULT_PHONE);
    }

    @Test
    void login() throws Exception {
        when(authService.login(DEFAULT_LOGIN, ENCRYPTED_PASSWORD))
                .thenReturn(true);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReqDto)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void register() throws Exception {

        when(authService.register(registerReqDto, Role.USER))
                .thenReturn(true);

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReqDto)))
                .andDo(print())
                .andExpect(status().isOk());
    }
}