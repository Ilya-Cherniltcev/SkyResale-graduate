package ru.skypro.homework.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class AdsCommentDto {
    @JsonProperty("pk")
    private Long id;
    private Long author;
    private OffsetDateTime createdAt;
    @JsonProperty("text")
    private String commentText;
}
