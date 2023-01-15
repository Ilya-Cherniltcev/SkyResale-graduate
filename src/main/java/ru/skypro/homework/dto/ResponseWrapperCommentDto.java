package ru.skypro.homework.dto;

import lombok.Data;

import java.util.List;
@Data
public class ResponseWrapperCommentDto {
    private Integer count;
    private List<AdsCommentDto> results;
}
