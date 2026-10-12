package com.vickey.skillforge.entity;

import com.vickey.skillforge.enums.SkillExchangeStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "skill_exchanges", indexes = {
        @Index(name = "idx_exchange_requester", columnList = "requester_id"),
        @Index(name = "idx_exchange_owner", columnList = "skill_owner_id")
})
@Getter
@Setter
public class SkillExchange {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "skill_owner_id", nullable = false)
    private User skillOwner;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "offered_skill_id", nullable = false)
    private Skill offeredSkill;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "requested_skill_id", nullable = false)
    private Skill requestedSkill;

    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private SkillExchangeStatus status = SkillExchangeStatus.PENDING;

    @Version
    @Column(nullable = false)
    private long version;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() { createdAt = updatedAt = Instant.now(); }

    @PreUpdate
    void onUpdate() { updatedAt = Instant.now(); }
}
