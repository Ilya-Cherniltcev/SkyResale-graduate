package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.model.Ads;
import ru.skypro.homework.model.AdsComment;

import java.util.List;

@Repository
public interface AdsRepository extends JpaRepository<Ads, Long> {
//    @Override
//    List<Ads> findAll();
    Ads findAdsById (Long id);


}
