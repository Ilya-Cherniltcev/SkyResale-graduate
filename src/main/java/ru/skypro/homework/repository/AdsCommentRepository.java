package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.model.AdsComment;

import java.util.List;

@Repository
public interface AdsCommentRepository extends JpaRepository<AdsComment,Long> {
    List<AdsComment> findAdsCommentByAdsId (Long adsId);
}
