package com.vickey.skillforge.service;

import com.vickey.skillforge.dto.CreateRatingRequestDto;
import com.vickey.skillforge.dto.RatingResponseDto;
import com.vickey.skillforge.entity.Rating;
import com.vickey.skillforge.entity.SkillExchange;
import com.vickey.skillforge.entity.User;
import com.vickey.skillforge.enums.SkillExchangeStatus;
import com.vickey.skillforge.exception.*;
import com.vickey.skillforge.repository.RatingRepository;
import com.vickey.skillforge.repository.SkillExchangeRepository;
import com.vickey.skillforge.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class RatingService {
    private final RatingRepository ratings;
    private final SkillExchangeRepository exchanges;
    private final UserRepository users;
    private final NotificationService notifications;

    public RatingService(RatingRepository ratings, SkillExchangeRepository exchanges,
                         UserRepository users, NotificationService notifications) {
        this.ratings = ratings;
        this.exchanges = exchanges;
        this.users = users;
        this.notifications = notifications;
    }

    @Transactional
    public RatingResponseDto create(String email, Long exchangeId, CreateRatingRequestDto request) {
        User rater = user(email);
        SkillExchange exchange = exchanges.findById(exchangeId)
                .orElseThrow(() -> new ExchangeNotFoundException("Exchange not found"));
        if (exchange.getStatus() != SkillExchangeStatus.COMPLETED) {
            throw new InvalidExchangeTransitionException("Only completed exchanges can be rated");
        }
        User rated;
        if (exchange.getRequester().getId().equals(rater.getId())) rated = exchange.getSkillOwner();
        else if (exchange.getSkillOwner().getId().equals(rater.getId())) rated = exchange.getRequester();
        else throw new ForbiddenOperationException("Only exchange participants can rate");
        if (ratings.existsByExchangeIdAndRaterId(exchangeId, rater.getId())) {
            throw new InvalidExchangeTransitionException("You have already rated this exchange");
        }
        Rating rating = new Rating();
        rating.setExchange(exchange);
        rating.setRater(rater);
        rating.setRatedUser(rated);
        rating.setScore(request.getScore());
        rating.setComment(request.getComment());
        Rating saved = ratings.save(rating);
        notifications.notify(rated, "RATING_RECEIVED", "You received a rating for a completed skill exchange");
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<RatingResponseDto> forUser(Long userId) {
        return ratings.findByRatedUserIdOrderByCreatedAtDesc(userId).stream().map(this::toDto).toList();
    }

    private User user(String email) {
        return users.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));
    }
    private RatingResponseDto toDto(Rating r) {
        return new RatingResponseDto(r.getId(), r.getExchange().getId(), r.getRater().getId(),
                r.getRatedUser().getId(), r.getScore(), r.getComment(), r.getCreatedAt());
    }
}
