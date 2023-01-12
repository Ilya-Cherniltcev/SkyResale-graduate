package ru.skypro.homework.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

/**
 * Класс Объявления
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "ads")
public class    Ads {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ads_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "author_user_id")
    private User author;

    @Column(name = "ads_title")
    private String title;

    @Column(name = "ads_description")
    private String description;



    @JoinColumn (name = "ads_image_id")
    @OneToOne (cascade = CascadeType.ALL)
    private AdsImage image;


    @Column(name = "ads_price")
    private Long price;

    @OneToMany
    @JsonIgnore
    @ToString.Exclude
    private Collection<AdsComment> adsComments;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this)
                != Hibernate.getClass(o)) return false;
        Ads ads = (Ads) o;
        return id != null && Objects.equals(id, ads.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}