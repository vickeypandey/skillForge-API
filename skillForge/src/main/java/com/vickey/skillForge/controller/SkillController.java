package com.vickey.skillForge.controller;


import com.vickey.skillForge.dto.SkillRequestDto;
import com.vickey.skillForge.dto.SkillResponseDto;
import com.vickey.skillForge.service.SkillService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/{userid}/skills")
public class SkillController {

    private final SkillService skillService;
    public SkillController(SkillService skillService)
    {
        this.skillService=skillService;
    }

    @PostMapping
    public ResponseEntity<SkillResponseDto> saveSkill(@PathVariable("userid") Long Userid, @RequestBody
                                                      @Valid SkillRequestDto skillRequestDto)
    {
        SkillResponseDto skillResponseDto= skillService.addSkill(skillRequestDto,Userid);
        return ResponseEntity.status(HttpStatus.CREATED).body(skillResponseDto);
    }

}
