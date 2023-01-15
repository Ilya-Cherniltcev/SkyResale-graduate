package ru.skypro.homework.dto;

import lombok.Data;

@Data
public class FullAdsDto {

    private String authorFirstName;
    private String authorLastName;
    private String description;
    private String email;
    private String[] image;
    private String phone;
    private long pk;
    private long price;
    private String title;

}
