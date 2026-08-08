package com.vickey.skillForge.repository;

import com.vickey.skillForge.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SkillRepository extends JpaRepository<Skill,Long> {

    List<Skill> findByUserId(Long userId);
    List<Skill> findBySkillNameContainingIgnoreCase(String skillName);
}
