package com.vickey.skillforge.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateProfileRequestDto {

    @NotBlank
    private String name;
}