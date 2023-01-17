package ru.skypro.homework.dto;

import lombok.Data;

@Data
public class AdsDto {

    private long author;
    private long pk; // * auto increasing *
    private String title;
    private long price;
    private String[] image;
}
