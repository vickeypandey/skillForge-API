package com.vickey.skillforge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.Instant;

@Data @AllArgsConstructor
public class NotificationResponseDto {
    private Long id;
    private String type;
    private String message;
    private boolean read;
    private Instant createdAt;
}
