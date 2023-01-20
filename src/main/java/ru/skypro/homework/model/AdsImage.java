package ru.skypro.homework.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

/**
 * Класс изображения из объявлений
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "ads_image")
public class AdsImage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ads_image_uuid")
    private UUID uuid;
    @ManyToOne
    @JoinColumn(name = "ads_id")
    private Ads ads;
    @Column(name = "file_path")
    private String filePath;

    @Column(name = "file_size")
    private Long filesize;

    @Column(name = "media_type")
    private String mediaType;

    @Lob
    @Column(name = "image_preview")
    private byte[] data;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AdsImage adsImage = (AdsImage) o;
        return uuid != null && Objects.equals(uuid, adsImage.uuid);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}