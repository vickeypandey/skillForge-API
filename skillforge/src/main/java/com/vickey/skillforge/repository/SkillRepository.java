package com.vickey.skillforge.repository;

import com.vickey.skillforge.entity.Skill;
import com.vickey.skillforge.entity.SkillCategory;
import com.vickey.skillforge.entity.SkillLevel;
import com.vickey.skillforge.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SkillRepository extends JpaRepository<Skill,Long> {

    List<Skill> findByUserId(Long userId);
    List<Skill> findBySkillNameContainingIgnoreCase(String skillName);
    List<Skill> findByCategory(
            SkillCategory category);
    List<Skill> findByLevel(
            SkillLevel level);
    List<Skill> findByCategoryAndLevel(
            SkillCategory category,
            SkillLevel level);
    List<Skill> findByUser(User user);
    boolean existsByUserAndSkillNameIgnoreCase(
            User user,
            String skillName);
}
