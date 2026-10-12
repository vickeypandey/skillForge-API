package com.vickey.skillforge.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateExchangeRequestDto {
    @NotNull
    private Long offeredSkillId;

    @NotNull
    private Long requestedSkillId;
}
