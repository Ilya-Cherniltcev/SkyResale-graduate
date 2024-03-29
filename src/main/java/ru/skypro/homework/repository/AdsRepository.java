package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.model.Ads;
import ru.skypro.homework.model.User;

import java.util.Optional;

@Repository
public interface AdsRepository extends JpaRepository<Ads, Long> {
    Optional<Ads> findAdsById (Long adsId);

    void deleteAllByAuthor(User user);
}
