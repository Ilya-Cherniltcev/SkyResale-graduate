package ru.skypro.homework.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Класс отзывы (комменты)
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "ads_comment")
public class AdsComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ads_id")
    private Ads adsId;

    @ManyToOne
    @JoinColumn(name = "author_user_id")
    private User author;

    @Column(name = "comment_created_at")
    private OffsetDateTime createdAt;

    @Column (name = "comment_text")
    private String commentText;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AdsComment that = (AdsComment) o;
        return adsId != null && Objects.equals(adsId, that.adsId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}