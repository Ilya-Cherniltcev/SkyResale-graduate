package ru.skypro.homework.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ads_image_id")
    private Long id;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "file_size")
    private Long filesize;

    @Column(name = "media_type")
    private String mediaType;

    @Lob
    @Column(name = "image_preview")
    private byte[] image;

    @ManyToOne
    @JoinColumn(name = "ads_id")
    private Ads adsId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AdsImage adsImage = (AdsImage) o;
        return id != null && Objects.equals(id, adsImage.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}