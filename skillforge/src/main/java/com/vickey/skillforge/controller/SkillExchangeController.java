package com.vickey.skillforge.controller;

import com.vickey.skillforge.dto.CreateExchangeRequestDto;
import com.vickey.skillforge.dto.SkillExchangeResponseDto;
import com.vickey.skillforge.service.SkillExchangeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SecurityRequirement(name = "bearerAuth")
@Validated
@RequestMapping("/api/v1/exchanges")
public class SkillExchangeController {
    private final SkillExchangeService service;
    public SkillExchangeController(SkillExchangeService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<SkillExchangeResponseDto> create(Authentication auth,
            @Valid @RequestBody CreateExchangeRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(auth.getName(), request));
    }

    @GetMapping("/me")
    public List<SkillExchangeResponseDto> mine(Authentication auth) { return service.mine(auth.getName()); }

    @GetMapping("/{id}")
    public SkillExchangeResponseDto get(Authentication auth, @PathVariable @NotNull Long id) {
        return service.get(auth.getName(), id);
    }

    @PatchMapping("/{id}/accept")
    public SkillExchangeResponseDto accept(Authentication auth, @PathVariable Long id) {
        return service.accept(auth.getName(), id);
    }

    @PatchMapping("/{id}/reject")
    public SkillExchangeResponseDto reject(Authentication auth, @PathVariable Long id) {
        return service.reject(auth.getName(), id);
    }

    @PatchMapping("/{id}/cancel")
    public SkillExchangeResponseDto cancel(Authentication auth, @PathVariable Long id) {
        return service.cancel(auth.getName(), id);
    }

    @PatchMapping("/{id}/complete")
    public SkillExchangeResponseDto complete(Authentication auth, @PathVariable Long id) {
        return service.complete(auth.getName(), id);
    }
}
