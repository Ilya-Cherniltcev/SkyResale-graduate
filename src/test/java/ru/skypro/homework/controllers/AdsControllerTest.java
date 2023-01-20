package ru.skypro.homework.controllers;

import net.minidev.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.skypro.homework.controller.UserController;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.ResponseWrapperAdsDto;
import ru.skypro.homework.dto.ResponseWrapperCommentDto;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.mapper.*;
import ru.skypro.homework.model.Ads;
import ru.skypro.homework.model.AdsComment;
import ru.skypro.homework.model.AdsImage;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.*;
import ru.skypro.homework.service.UserService;
import ru.skypro.homework.service.impl.AdsServiceImpl;
import ru.skypro.homework.service.impl.AuthServiceImpl;
import ru.skypro.homework.service.impl.UserServiceImpl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.skypro.homework.ConstantsForTests.*;

@WebMvcTest(AdsControllerTest.class)
public class AdsControllerTest {
    Ads ads = new Ads();
    Ads updateAds = new Ads();
    AdsDto adsDto = new AdsDto();
    AdsDto updateAdsDto = new AdsDto();
    JSONObject jsonObject = new JSONObject();
    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;
    @SpyBean
    private AdsServiceImpl adsService;
    @SpyBean
    private AuthServiceImpl authService;
    @MockBean
    AdsCommentRepository adsCommentRepository;
    @MockBean
    AdsImageRepository adsImageRepository;
    @MockBean
    AdsRepository adsRepository;
    @MockBean
    AdsCommentMapper adsCommentMapper;
    @MockBean
    AdsMapper adsMapper;
//    @MockBean
//    ResponseWrapperAdsDto responseWrapperAdsDto;
    @MockBean
    ResponseWrapperAdsMapper responseWrapperAdsMapper;
     @MockBean
    ResponseWrapperCommentMapper responseWrapperCommentMapper;
    @MockBean
    ResponseWrapperCommentDto responseWrapperCommentDto;

    @MockBean
    ImageMapper imageMapper;
    @MockBean
    Authentication authentication;
    @MockBean
    AuthenticationManager authenticationManager;
    @MockBean
    PasswordEncoder passwordEncoder;
    @MockBean
    UserService userService;
    User user = new User();
    AdsImage adsImage= new AdsImage();
    AdsComment adsComment = new AdsComment();
    UUID uuid = new UUID(1,1);
    List<Ads> adsList;
    List<AdsDto> adsDtoList;
    List<AdsImage> adsImageList;

    ResponseWrapperAdsDto responseWrapperAdsDto = new ResponseWrapperAdsDto();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        user.setId(DEFAULT_USER_ID);
        user.setLogin(DEFAULT_USERNAME);
        user.setFirstName(DEFAULT_FIRSTNAME);
        user.setLastName(DEFAULT_LASTNAME);
        user.setPhoneNumber(DEFAULT_PHONE);

        ads.setId(DEFAULT_ADS_ID);
        ads.setTitle(DEFAULT_ADS_TITLE);
        ads.setPrice(DEFAULT_ADS_PRICE);
        ads.setAuthor(user);
        ads.setDescription(DEFAULT_ADS_DESCRIPTION);

        adsImage.setAds(ads);
        adsImage.setUuid(uuid);
        adsImage.setFilesize(DEFAULT_FILE_SIZE);
        adsImage.setMediaType(DEFAULT_MEDIA_TYPE);
        adsImage.setFilePath(DEFAULT_FILE_PATH);
        adsImage.setData(DEFAULT_DATA);
        adsImageList = List.of(adsImage);

        ads.setImage(adsImageList);

        adsComment.setAds(ads);
        adsComment.setAuthor(user);
        adsComment.setCommentText(DEFAULT_ADS_COMMENT_TEXT);
        adsComment.setId(DEFAULT_ADS_COMMENT_ID);

        adsList = List.of(ads);

        adsDto.setPrice(DEFAULT_ADS_PRICE);
        adsDto.setAuthor(ads.getAuthor().getId());
        adsDto.setTitle(DEFAULT_ADS_TITLE);
        adsDto.setPk(DEFAULT_ADS_DTO_PK);
        adsDto.setImage(DEFAULT_ADS_DTO_IMAGE_ARRAY);

        adsDtoList = List.of(adsDto);

        responseWrapperAdsDto.setCount(1);
        responseWrapperAdsDto.setResults(adsDtoList);

//        updatedUser.setId(DEFAULT_USER_ID);
//        updatedUser.setLogin(DEFAULT_USERNAME);
//        updatedUser.setFirstName(UPDATED_FIRSTNAME);
//        updatedUser.setLastName(DEFAULT_LASTNAME);
//        updatedUser.setPhoneNumber(UPDATED_PHONE);
//
//        userDto.setUserId(DEFAULT_USER_ID);
//        userDto.setLogin(DEFAULT_USERNAME);
//        userDto.setFirstName(DEFAULT_FIRSTNAME);
//        userDto.setLastName(DEFAULT_LASTNAME);
//        userDto.setPhoneNumber(DEFAULT_PHONE);
//
//        updatedUserDto.setUserId(DEFAULT_USER_ID);
//        updatedUserDto.setLogin(DEFAULT_USERNAME);
//        updatedUserDto.setFirstName(UPDATED_FIRSTNAME);
//        updatedUserDto.setLastName(DEFAULT_LASTNAME);
//        updatedUserDto.setPhoneNumber(UPDATED_PHONE);
// -----------
        jsonObject.put("id", DEFAULT_USER_ID);
        jsonObject.put("login", DEFAULT_USERNAME);
        jsonObject.put("firstName", UPDATED_FIRSTNAME);
    }

    @WithMockUser(username = DEFAULT_USERNAME, authorities = "ADMIN")
    @Test
    void testGetAllAds() throws Exception {
        when(authentication.getName()).thenReturn(DEFAULT_USERNAME);
        when(adsRepository.findAll()).thenReturn(adsList);
        when(responseWrapperAdsMapper.toResponseWrapperAdsDto(1,adsDtoList))
                .thenReturn(responseWrapperAdsDto);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/ads")
                        .content(jsonObject.toJSONString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
//                .andExpect(jsonPath("$.title").value(DEFAULT_ADS_TITLE))
//                .andExpect(jsonPath("$.price").value(DEFAULT_ADS_PRICE));
        }

}
