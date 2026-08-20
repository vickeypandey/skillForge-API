package com.vickey.skillforge.dto;


import com.vickey.skillforge.entity.SkillCategory;
import com.vickey.skillforge.entity.SkillLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SkillRequestDto {

    @NotBlank
    @Size(max = 100)
    private String skillName;
    @Size(max = 500)
    private String description;
    @NotNull
    private SkillCategory category;
    @NotNull
    private SkillLevel level;

}
