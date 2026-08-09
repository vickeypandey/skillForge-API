package com.vickey.skillforge.controller;

import com.vickey.skillforge.dto.SkillRequestDto;
import com.vickey.skillforge.dto.SkillResponseDto;
import com.vickey.skillforge.service.SkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Skill APIs", description = "APIs for skill search, update, delete and pagination")

@RestController
@RequestMapping("/api/skills")
public class SkillManagementController {

    private final SkillService skillService;

    public SkillManagementController(SkillService skillService) {
        this.skillService = skillService;
    }


    @Operation(summary = "Get skill by skill ID")
    @GetMapping("/{skillId}")
    public ResponseEntity<SkillResponseDto> getSkillById(
            @PathVariable Long skillId) {

        SkillResponseDto skillResponseDto =
                skillService.getSkillById(skillId);

        return ResponseEntity.ok(skillResponseDto);
    }

    @Operation(summary = "update using skill id")
    @PutMapping("/{skillId}")
    public ResponseEntity<SkillResponseDto> updateSkillById(@PathVariable Long skillId, @RequestBody @Valid SkillRequestDto skillRequestDto) {
        SkillResponseDto skillResponseDto = skillService.updateSkillById(skillId, skillRequestDto);
        return ResponseEntity.ok(skillResponseDto);
    }

    @Operation(summary = "delete using skill id")
    @DeleteMapping("/{skillId}")
    public ResponseEntity<Void> deleteSkillById(@PathVariable Long skillId) {
        skillService.deleteSkillById(skillId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Search skill , pagination and sorting")
    @GetMapping("/search")
    public ResponseEntity<List<SkillResponseDto>> searchBySkillName(@RequestParam String skillName) {
        List<SkillResponseDto> responseDtos = skillService.searchSkillsByName(skillName);
        return ResponseEntity.ok(responseDtos);

    }

    @Operation(summary = "search skill using page number and size")
    @GetMapping
    public ResponseEntity<Page<SkillResponseDto>> getAllSkills(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "asc") String direction) {
        Page<SkillResponseDto> skillResponseDtos = skillService.getAllSkillsPaginated(page, size, sortBy, direction);
        return ResponseEntity.ok(skillResponseDtos);

    }
}



