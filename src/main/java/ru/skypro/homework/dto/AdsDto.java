package ru.skypro.homework.dto;

import lombok.Data;
import ru.skypro.homework.model.AdsImage;

import java.util.Collection;

@Data
public class AdsDto {
    private long adsId; // * auto increasing *
    private String title;
    private String description;
    private long price;
//    private String image;
    private Collection<AdsImage> adsImages;
}
