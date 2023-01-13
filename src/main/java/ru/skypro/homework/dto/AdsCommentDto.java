package ru.skypro.homework.dto;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class AdsCommentDto {
    private Long id;
    private Long ads;
    private Long author;
    private OffsetDateTime createdAt;
    private String commentText;
}
