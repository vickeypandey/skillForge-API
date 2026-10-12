package com.vickey.skillforge.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequestDto {


    @NotBlank
    @Size(max = 100)
    private String name;


    @NotBlank
    @Size(min = 8 , max = 72)
    private String password;

    @Email
    @NotBlank
    @Size(max = 254)
    private String email;


}
