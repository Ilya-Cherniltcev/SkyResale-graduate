package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.model.Ads;
import ru.skypro.homework.model.AdsImage;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface AdsImageRepository extends JpaRepository<AdsImage,Long> {

    Optional<AdsImage> findAdsImageById(long adsImageId);

    Collection<AdsImage> findAdsImagesByAds(Ads ads);

    void deleteAdsImagesByAds(Ads ads);
}
