package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.model.Ads;
import ru.skypro.homework.model.AdsComment;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdsCommentRepository extends JpaRepository<AdsComment,Long> {
    List<AdsComment> findAdsCommentByAdsId (Ads ads);

    Optional<AdsComment> findAdsCommentById(Long commentId);
}
