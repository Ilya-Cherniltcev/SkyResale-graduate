package ru.skypro.homework.dto;

import lombok.Data;

import java.time.OffsetDateTime;
@Data
public class AdsCommentDto {
    private long adsCommentId;

    private OffsetDateTime createdAt;

    private String commentText;
}
