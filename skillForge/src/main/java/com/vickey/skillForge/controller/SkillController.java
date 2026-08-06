package com.vickey.skillForge.controller;


import com.vickey.skillForge.dto.SkillRequestDto;
import com.vickey.skillForge.dto.SkillResponseDto;
import com.vickey.skillForge.service.SkillService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/skills")

public class SkillController {

    private final SkillService skillService;
    public SkillController(SkillService skillService)
    {
        this.skillService=skillService;
    }

    @PostMapping
    public ResponseEntity<SkillResponseDto> saveSkill(@PathVariable Long userId, @RequestBody
                                                      @Valid SkillRequestDto skillRequestDto)
    {
        SkillResponseDto skillResponseDto= skillService.addSkill(skillRequestDto,userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(skillResponseDto);
    }



    @GetMapping
    public ResponseEntity<List<SkillResponseDto>> getById(@PathVariable Long userId)
    {
        Long id=userId;
        List <SkillResponseDto> skillResponseDtos= skillService.getAllSkillsOfUser(id);
        return ResponseEntity.ok(skillResponseDtos);
    }

}
