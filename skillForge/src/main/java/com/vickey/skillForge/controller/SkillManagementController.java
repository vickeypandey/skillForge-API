package com.vickey.skillForge.controller;

import com.vickey.skillForge.dto.SkillRequestDto;
import com.vickey.skillForge.dto.SkillResponseDto;
import com.vickey.skillForge.service.SkillService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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

    @PutMapping("/{id}")
    public ResponseEntity<SkillResponseDto> updateSkillById(@PathVariable Long id, @RequestBody @Valid SkillRequestDto skillRequestDto) {
        SkillResponseDto skillResponseDto = skillService.updateSkillById(id, skillRequestDto);
        return ResponseEntity.ok(skillResponseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSkillById(@PathVariable Long id) {
        skillService.deleteSkillById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<SkillResponseDto>> searchBySkillName(@RequestParam String skillname) {
        List<SkillResponseDto> responseDtos = skillService.findByskillname(skillname);
        return ResponseEntity.ok(responseDtos);

    }

    @GetMapping
    public ResponseEntity<Page<SkillResponseDto>> getAllSkills(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortby, @RequestParam(defaultValue = "asc") String sortingDirection) {
        Page<SkillResponseDto> skillResponseDtos = skillService.getAllSkillsPaginated(page, size, sortby, sortingDirection);
        return ResponseEntity.ok(skillResponseDtos);

    }
}



