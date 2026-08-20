package com.vickey.skillforge.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Getter
@Setter
public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String skillName;
    @Column(length = 500)
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SkillCategory category;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SkillLevel level;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}


