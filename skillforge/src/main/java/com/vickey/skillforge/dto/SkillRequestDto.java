package com.vickey.skillforge.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SkillRequestDto {

    @NotBlank
    private String skillName;

}
