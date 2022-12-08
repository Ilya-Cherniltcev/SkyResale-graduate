package ru.skypro.homework.dto;

import lombok.Data;

@Data
public class AdsDto {
    private long adsId; // * auto increasing *
    private String title;
    private String description;
    private long price;
    private String pk; // this is comment to ads
    private byte[] image;
}
