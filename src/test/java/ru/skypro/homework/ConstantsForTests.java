package ru.skypro.homework;

import ru.skypro.homework.model.Ads;
import ru.skypro.homework.model.AdsImage;
import ru.skypro.homework.model.User;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class ConstantsForTests {
    public static final String DTO_IMAGE_STRING1 = "image1";
    public static final String DTO_IMAGE_STRING2 = "image2";
    public static final String[] DTO_IMAGE_ARRAY = {DTO_IMAGE_STRING1, DTO_IMAGE_STRING2};

    public static final String DEFAULT_LOGIN = "user@gmail.com";
    public static final String DEFAULT_PASSWORD = "password";
    public static final String ENCRYPTED_PASSWORD =
            "{bcrypt}$2a$10$YEyX.yAuIE8MiuOjF63xTe6LDSkbrLXdc.Dz4JteJHHe6uSkfvJPO";
    public static final Long DEFAULT_USER_ID = 1L;
    public static final String DEFAULT_USERNAME = DEFAULT_LOGIN;
    public static final String DEFAULT_FIRSTNAME = "JOHN";
    public static final String UPDATED_FIRSTNAME = "JAMES";
    public static final String DEFAULT_LASTNAME = "DOE";
    public static final String DEFAULT_PHONE = "+791111111111";
    public static final String UPDATED_PHONE = "+792222222222";

    public static final String CURRENT_PASSWORD = "password_1";
    public static final String NEW_PASSWORD = "password_2";
    public static final Long DEFAULT_USER_AVATAR_ID = 1L;
    public static final byte[] DEFAULT_USER_AVATAR = "f6d73k9r".getBytes(StandardCharsets.UTF_8);

    // -----------------------------------------------
    public static final Long DEFAULT_ADS_ID = 1L;
    public static final String DEFAULT_ADS_TITLE = "Elephant";
    public static final String DEFAULT_ADS_DESCRIPTION = "Very big and cool";
    public static final Long DEFAULT_ADS_PRICE = 82L;
    public static final byte[] DEFAULT_DATA = {0, 1};
    public static final String DEFAULT_FILE_PATH = "";
    public static final String DEFAULT_MEDIA_TYPE = "image/*";
    public static final Long DEFAULT_FILE_SIZE = 1L;
    public static final String DEFAULT_ADS_COMMENT_TEXT = "So-so";
    public static final Long DEFAULT_ADS_COMMENT_ID = 1L;

    public static final Long DEFAULT_ADS_DTO_PK = 1L;
    public static final String[] DEFAULT_ADS_DTO_IMAGE_ARRAY = {"0","1"};

}
