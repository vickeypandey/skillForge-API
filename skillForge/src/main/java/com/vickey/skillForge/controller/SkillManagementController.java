package com.vickey.skillForge.controller;

import com.vickey.skillForge.dto.SkillResponseDto;
import com.vickey.skillForge.service.SkillService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/skills")
public class SkillManagementController {

    private final SkillService skillService;

    public SkillManagementController(SkillService skillService) {
        this.skillService = skillService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<SkillResponseDto> getSkillById(
            @PathVariable Long id) {

        SkillResponseDto skillResponseDto =
                skillService.getSkillById(id);

        return ResponseEntity.ok(skillResponseDto);
    }
}
