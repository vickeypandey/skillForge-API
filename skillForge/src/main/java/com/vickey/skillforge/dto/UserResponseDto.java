package com.vickey.skillforge.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserResponseDto {

    private String name;

    private String email;

    private Long id;
    List<SkillResponseDto> skills;

}
