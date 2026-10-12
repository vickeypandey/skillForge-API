package com.vickey.skillforge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.Instant;

@Data @AllArgsConstructor
public class RatingResponseDto {
    private Long id;
    private Long exchangeId;
    private Long raterId;
    private Long ratedUserId;
    private int score;
    private String comment;
    private Instant createdAt;
}
