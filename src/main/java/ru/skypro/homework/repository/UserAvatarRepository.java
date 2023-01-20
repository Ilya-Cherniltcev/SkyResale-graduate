package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.model.User;
import ru.skypro.homework.model.UserAvatar;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserAvatarRepository extends JpaRepository<UserAvatar,Long> {

    Optional<UserAvatar> findUserAvatarByUser(User user);

    Optional<UserAvatar> findUserAvatarByAvatarUuid(UUID avatarUuid);
    void deleteUserAvatarByUser(User user);
}
