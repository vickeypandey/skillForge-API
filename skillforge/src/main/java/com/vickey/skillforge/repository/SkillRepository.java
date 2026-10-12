package com.vickey.skillforge.repository;

import com.vickey.skillforge.entity.Skill;
import com.vickey.skillforge.entity.SkillCategory;
import com.vickey.skillforge.entity.SkillLevel;
import com.vickey.skillforge.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SkillRepository extends JpaRepository<Skill,Long> {

    List<Skill> findByUserId(Long userId);
    Page<Skill> findBySkillNameContainingIgnoreCase(String skillName, Pageable pageable);
    Page<Skill> findByCategory(SkillCategory category, Pageable pageable);
    Page<Skill> findByLevel(SkillLevel level, Pageable pageable);
    List<Skill> findByCategoryAndLevel(
            SkillCategory category,
            SkillLevel level);
    List<Skill> findByUser(User user);
    boolean existsByUserAndSkillNameIgnoreCase(
            User user,
            String skillName);
}
