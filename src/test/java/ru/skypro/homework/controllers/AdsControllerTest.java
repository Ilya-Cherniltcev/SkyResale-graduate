package ru.skypro.homework.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.skypro.homework.controller.AdsController;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.mapper.*;
import ru.skypro.homework.model.Ads;
import ru.skypro.homework.model.AdsComment;
import ru.skypro.homework.model.AdsImage;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.*;
import ru.skypro.homework.service.impl.AdsServiceImpl;
import ru.skypro.homework.service.impl.AuthServiceImpl;
import ru.skypro.homework.service.impl.UserServiceImpl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.skypro.homework.ConstantsForTests.*;

@WebMvcTest(AdsController.class)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdsControllerTest {
    Ads ads = new Ads();
    Ads updateAds = new Ads();
    AdsDto adsDto = new AdsDto();
    CreateAdsDto createAdsDto = new CreateAdsDto();
    AdsDto updateAdsDto = new AdsDto();
    JSONObject jsonObject = new JSONObject();
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @SpyBean
    private AdsServiceImpl adsService;
    @MockBean
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
    UserServiceImpl userService;
    User user = new User();
    FullAdsDto fullAdsDto = new FullAdsDto();
    AdsImage adsImage = new AdsImage();
    AdsComment adsComment = new AdsComment();
    UUID uuid = new UUID(1, 1);
    List<Ads> adsList;
    List<AdsDto> adsDtoList;
    List<AdsImage> adsImageList;

    ResponseWrapperAdsDto responseWrapperAdsDto = new ResponseWrapperAdsDto();

    @BeforeEach
    void setUp() {

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

        createAdsDto.setTitle(DEFAULT_ADS_TITLE);
        createAdsDto.setPrice(DEFAULT_ADS_PRICE);
        createAdsDto.setDescription(DEFAULT_ADS_DESCRIPTION);

        responseWrapperAdsDto.setCount(1);
        responseWrapperAdsDto.setResults(adsDtoList);

        fullAdsDto.setPk(DEFAULT_ADS_ID);
        fullAdsDto.setDescription(DEFAULT_ADS_DESCRIPTION);
        fullAdsDto.setTitle(DEFAULT_ADS_TITLE);
        fullAdsDto.setPrice(DEFAULT_ADS_PRICE);
        fullAdsDto.setImage(DEFAULT_ADS_DTO_IMAGE_ARRAY);

        jsonObject.put("count", 1);
        jsonObject.put("pk", DEFAULT_ADS_ID);
        jsonObject.put("title", DEFAULT_ADS_TITLE);
        jsonObject.put("price", DEFAULT_ADS_PRICE);
    }

    @WithMockUser(username = DEFAULT_USERNAME, authorities = "USER")
    @Test
    void testGetAllAds() throws Exception {
        when(adsRepository.findAll())
                .thenReturn(adsList);
        when(responseWrapperAdsMapper.toResponseWrapperAdsDto(adsDtoList.size(), adsDtoList))
                .thenReturn(responseWrapperAdsDto);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/ads")
                        .content(jsonObject.toJSONString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @WithMockUser(username = DEFAULT_USERNAME, authorities = "USER")
    @Test
    public void testDeleteAds() throws Exception {
        when(userService.getUserFromAuthentication())
                .thenReturn(user);
        when(adsRepository.findAdsById(any())).thenReturn(Optional.ofNullable(ads));
        doNothing().when(adsRepository).deleteById(any());
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/ads/{adsId}", ads.getId())
                        .content(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @WithMockUser(username = DEFAULT_USERNAME, authorities = "USER")
    @Test
    public void testGetFullAds() throws Exception {
        when(adsRepository.findAdsById(any())).thenReturn(Optional.ofNullable(ads));
        when(adsMapper.toFullAdsDto(ads)).thenReturn(fullAdsDto);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/ads/{adsId}", ads.getId())
                        .content(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockUser(username = DEFAULT_USERNAME, authorities = "USER")
    @Test
    public void testGetAdsMe() throws Exception {
        when(adsRepository.findAll()).thenReturn(adsList);
        when(responseWrapperAdsMapper.toResponseWrapperAdsDto(adsDtoList.size(), adsDtoList))
                .thenReturn(responseWrapperAdsDto);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/ads/me")
                        .content(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockUser(username = DEFAULT_USERNAME, authorities = "USER")
    @Disabled
    public void testAddAds() throws Exception {
        when(userService.getUserFromAuthentication()).thenReturn(user);
        when(adsRepository.save(any())).thenReturn(ads);
        when(adsMapper.toDto(any())).thenReturn(adsDto);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/ads")
                      //  .queryParam("properties", String.valueOf(createAdsDto))
                        .content(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .accept(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @WithMockUser(username = DEFAULT_USERNAME, authorities = "USER")
    @Disabled
    public void testUpdateAds() throws Exception {
        when(adsRepository.findAdsById(any())).thenReturn(Optional.ofNullable(ads));
        when(adsMapper.updAds(createAdsDto,ads)).thenReturn(ads);
        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/ads/{adsId}", ads.getId())
                        .content(objectMapper.writeValueAsString(createAdsDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
