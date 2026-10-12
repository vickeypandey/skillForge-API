package com.vickey.skillforge.controller;

import com.vickey.skillforge.dto.NotificationResponseDto;
import com.vickey.skillforge.service.NotificationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/v1/notifications")
public class NotificationController {
    private final NotificationService service;
    public NotificationController(NotificationService service) { this.service = service; }

    @GetMapping
    public List<NotificationResponseDto> mine(Authentication auth) { return service.mine(auth.getName()); }

    @PatchMapping("/{id}/read")
    public NotificationResponseDto markRead(Authentication auth, @PathVariable Long id) {
        return service.markRead(auth.getName(), id);
    }
}
