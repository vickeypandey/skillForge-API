package com.vickey.skillforge.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Entity
@Table(name = "ratings", uniqueConstraints = @UniqueConstraint(
        name = "uk_rating_exchange_rater", columnNames = {"exchange_id", "rater_id"}),
        indexes = @Index(name = "idx_rating_rated_user", columnList = "rated_user_id"))
@Getter @Setter
public class Rating {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "exchange_id", nullable = false)
    private SkillExchange exchange;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "rater_id", nullable = false)
    private User rater;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "rated_user_id", nullable = false)
    private User ratedUser;
    @Column(nullable = false)
    private int score;
    @Column(length = 1000)
    private String comment;
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
    @PrePersist void onCreate() { createdAt = Instant.now(); }
}
