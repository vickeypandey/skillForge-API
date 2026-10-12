package com.vickey.skillforge.repository;

import com.vickey.skillforge.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    boolean existsByExchangeIdAndRaterId(Long exchangeId, Long raterId);
    List<Rating> findByRatedUserIdOrderByCreatedAtDesc(Long userId);
}
