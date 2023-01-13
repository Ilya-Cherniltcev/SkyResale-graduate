package ru.skypro.homework.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

/**
 * Класс аватар пользователя
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
@Table(name = "users_avatars")
public class UserAvatar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "avatar_id")
    private Long avatarId;
    @Column(name = "file_path")
    private String filePath;

    @Column(name = "file_size")
    private Long filesize;

    @Column(name = "media_type")
    private String mediaType;

    @Lob
    @Column(name = "avatar_preview")
    private byte[] data;

    @OneToOne
    @JoinColumn(name = "author_id")
    private User user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UserAvatar that = (UserAvatar) o;
        return avatarId != null && Objects.equals(avatarId, that.avatarId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}