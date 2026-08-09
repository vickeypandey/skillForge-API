package com.vickey.skillforge.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequestDto {


    @NotBlank
    private String name;


    @NotBlank
    @Size(min = 8 , max = 16)
    private String password;

    @Email
    @NotBlank
    private String email;
}
