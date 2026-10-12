package com.vickey.skillforge.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateRatingRequestDto {
    @Min(1) @Max(5) private int score;
    @Size(max = 1000) private String comment;
}
