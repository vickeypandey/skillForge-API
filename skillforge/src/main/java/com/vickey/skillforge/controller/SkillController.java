package com.vickey.skillforge.controller;


import com.vickey.skillforge.dto.SkillRequestDto;
import com.vickey.skillforge.dto.SkillResponseDto;
import com.vickey.skillforge.service.SkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "User Skill APIs", description = "APIs for managing skills of a particular user")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/users/{userId}/skills")

public class SkillController {

    private final SkillService skillService;
    public SkillController(SkillService skillService)
    {
        this.skillService=skillService;
    }

    @Operation(summary = "Get skills by userID")
    @GetMapping
    public ResponseEntity<List<SkillResponseDto>> getById(@PathVariable Long userId)
    {

        List <SkillResponseDto> skillResponseDtos= skillService.getAllSkillsOfUser(userId);
        return ResponseEntity.ok(skillResponseDtos);
    }

}
