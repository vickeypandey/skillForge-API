package com.vickey.skillforge.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequestDto {

    @NotBlank
    @Size(max = 100)
    private String name;
}
