package com.vickey.skillforge.controller;

import com.vickey.skillforge.dto.CreateRatingRequestDto;
import com.vickey.skillforge.dto.RatingResponseDto;
import com.vickey.skillforge.service.RatingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/v1")
public class RatingController {
    private final RatingService service;
    public RatingController(RatingService service) { this.service = service; }

    @PostMapping("/exchanges/{exchangeId}/ratings")
    @ResponseStatus(HttpStatus.CREATED)
    public RatingResponseDto create(Authentication auth, @PathVariable Long exchangeId,
                                    @Valid @RequestBody CreateRatingRequestDto request) {
        return service.create(auth.getName(), exchangeId, request);
    }

    @GetMapping("/users/{userId}/ratings")
    public List<RatingResponseDto> forUser(@PathVariable Long userId) { return service.forUser(userId); }
}
