package ru.skypro.homework.dto;

import lombok.Data;
import ru.skypro.homework.model.Ads;
import ru.skypro.homework.model.User;

import java.time.OffsetDateTime;
@Data
public class AdsCommentDto {
    private long adsCommentId;

    private Ads adsId;

    private User author;

    private OffsetDateTime createdAt;

    private String commentText;
}
