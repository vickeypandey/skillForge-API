package com.vickey.skillforge.controller;


import com.vickey.skillforge.dto.SkillRequestDto;
import com.vickey.skillforge.dto.SkillResponseDto;
import com.vickey.skillforge.service.SkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "User Skill APIs", description = "APIs for managing skills of a particular user")
@RestController
@RequestMapping("/api/users/{userId}/skills")

public class SkillController {

    private final SkillService skillService;
    public SkillController(SkillService skillService)
    {
        this.skillService=skillService;
    }

    @Operation(summary = "Set skills of a user by ID")
    @PostMapping
    public ResponseEntity<SkillResponseDto> saveSkill(@PathVariable Long userId, @RequestBody
                                                      @Valid SkillRequestDto skillRequestDto)
    {
        SkillResponseDto skillResponseDto= skillService.addSkill(skillRequestDto,userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(skillResponseDto);
    }


    @Operation(summary = "Get skills by userID")
    @GetMapping
    public ResponseEntity<List<SkillResponseDto>> getById(@PathVariable Long userId)
    {

        List <SkillResponseDto> skillResponseDtos= skillService.getAllSkillsOfUser(userId);
        return ResponseEntity.ok(skillResponseDtos);
    }

}
