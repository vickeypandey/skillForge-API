package com.vickey.skillforge.dto;

import com.vickey.skillforge.enums.SkillExchangeStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.Instant;

@Data
@AllArgsConstructor
public class SkillExchangeResponseDto {
    private Long id;
    private Long requesterId;
    private String requesterName;
    private Long skillOwnerId;
    private String skillOwnerName;
    private Long offeredSkillId;
    private String offeredSkillName;
    private Long requestedSkillId;
    private String requestedSkillName;
    private SkillExchangeStatus status;
    private Instant createdAt;
    private Instant updatedAt;
}
