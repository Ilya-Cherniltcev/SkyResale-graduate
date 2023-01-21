package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.model.Ads;
import ru.skypro.homework.model.AdsImage;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdsImageRepository extends JpaRepository<AdsImage,Long> {

    Optional<AdsImage> findAdsImageByUuid(UUID adsImageUuid);

    void deleteAdsImagesByAds(Ads ads);
}
