package com.vickey.skillforge.dto;


import com.vickey.skillforge.entity.SkillCategory;
import com.vickey.skillforge.entity.SkillLevel;
import lombok.Data;

@Data
public class SkillResponseDto {
    private Long id;
    private String skillName;
    private String description;
    private SkillCategory category;
    private SkillLevel level;
    private Long userId;
    private String userName;

}
