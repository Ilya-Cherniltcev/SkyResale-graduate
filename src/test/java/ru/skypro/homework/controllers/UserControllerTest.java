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
import org.springframework.mock.web.MockMultipartFile;


import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.skypro.homework.controller.UserController;

import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.model.User;
import ru.skypro.homework.model.UserAvatar;
import ru.skypro.homework.repository.AdsCommentRepository;
import ru.skypro.homework.repository.UserAvatarRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.impl.UserServiceImpl;

import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.skypro.homework.ConstantsForTests.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(UserController.class)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    User user = new User();
    UserDto userDto = new UserDto();
    User userAvatarEntity = new User();

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    @MockBean
    private UserAvatar avatar;
    @MockBean
    private UserServiceImpl userService;
    @MockBean
    private Authentication authentication;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private UserMapper userMapper;
    @MockBean
    private UserAvatarRepository userAvatarRepository;
    @MockBean
    private AdsCommentRepository adsCommentRepository;



    @Autowired
    UserControllerTest(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @BeforeEach


        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/users/me")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(user.getLogin()))
                .andExpect(jsonPath("$.firstName").value(user.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(user.getLastName()))
                .andExpect(jsonPath("$.phone").value(UPDATED_PHONE));

    }

    @Test
    void testSetPassword() throws Exception {
        NewPasswordDto passwordDto = new NewPasswordDto();
        passwordDto.setCurrentPassword(CURRENT_PASSWORD);
        passwordDto.setNewPassword(NEW_PASSWORD);
        when(userService.setPassword(any()))
                .thenReturn(passwordDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/users/set_password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentPassword")
                        .value(passwordDto.getCurrentPassword()))
                .andExpect(jsonPath("$.newPassword")
                        .value(passwordDto.getNewPassword()));
    }
=======
                .andExpect(status().isOk());
//                .andExpect(jsonPath("$.login").value(createUserDto.getLogin()))
//                .andExpect(jsonPath("$.password").value(createUserDto.getPassword()))
//                .andExpect(jsonPath("$.firstName").value(createUserDto.getFirstName()))
//                .andExpect(jsonPath("$.lastName").value(createUserDto.getLastName()))
//                .andExpect(jsonPath("$.phoneNumber").value(createUserDto.getPhoneNumber()));
    }

    @Test
    void updateUser() throws Exception {
//        when(userRepository.save(userMapper.toUser(createUserDto)))
//                .thenReturn(user);


    @Test
    void testGetUser() throws Exception {
        when(authentication.getName())
                .thenReturn(DEFAULT_USERNAME);
        when(userRepository.findUserByLoginIgnoreCase(any()))
                .thenReturn(Optional.ofNullable(user));
        when(userService.getUserMe())
                .thenReturn(userDto);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/users/me")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(user.getLogin()))
                .andExpect(jsonPath("$.firstName").value(user.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(user.getLastName()))
                .andExpect(jsonPath("$.phone").value(user.getPhoneNumber()));
    }

    @Test
    public void testDeleteUser() throws Exception {
        when(authentication.getName())
                .thenReturn(DEFAULT_USERNAME);
        when(userRepository.findUserByLoginIgnoreCase(any()))
                .thenReturn(Optional.ofNullable(user));
        userService.removeUser(user.getId());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/users/{id}", user.getId()))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test   // ===============================================================
    public void testGetImage() throws Exception {
        when(userRepository.findUserByLoginIgnoreCase(DEFAULT_USERNAME))
                .thenReturn(Optional.of(userAvatarEntity));
        when(userAvatarRepository.findUserAvatarByAvatarUuid(any()))
                .thenReturn(Optional.ofNullable(avatar));
        userService.getAvatar(avatar.getAvatarUuid());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/users/image/{avatarUuid}",
                                avatar.getAvatarUuid()))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateImage() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "image.png",
                MediaType.IMAGE_PNG_VALUE, DEFAULT_USER_AVATAR);

        when(userRepository.findUserByLoginIgnoreCase(DEFAULT_USERNAME))
                .thenReturn(Optional.of(userAvatarEntity));
        when(userRepository.save(any(User.class)))
                .thenReturn(userAvatarEntity);
        when(userAvatarRepository.findUserAvatarByUser(any(User.class)))
                .thenReturn(Optional.of(avatar));
        when(userAvatarRepository.save(any(UserAvatar.class)))
                .thenReturn(avatar);

        MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .multipart("/users/me/image");
        builder.with
                (request -> {
                    request.setMethod("PATCH");
                    return request;
                });

        mockMvc.perform(builder.file(image))
                .andDo(print())
                .andExpect(status().isOk());
    }
}