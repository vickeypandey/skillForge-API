package com.vickey.skillForge.service;

import com.vickey.skillForge.dto.SkillRequestDto;
import com.vickey.skillForge.dto.SkillResponseDto;
import com.vickey.skillForge.entity.Skill;
import com.vickey.skillForge.entity.User;
import com.vickey.skillForge.exception.UserNotFoundException;
import com.vickey.skillForge.repository.SkillRepository;
import com.vickey.skillForge.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class SkillService {

    private final SkillRepository skillRepository;
    private final UserRepository userRepository;

    public SkillService(SkillRepository skillRepository,UserRepository userRepository)
    {
        this.skillRepository=skillRepository;
        this.userRepository=userRepository;
    }

    public SkillResponseDto addSkill(SkillRequestDto skillRequestDto, Long userid) {
        User user = userRepository.findById(userid).orElseThrow(() -> new UserNotFoundException("user not found with:" + userid));
        Skill skill = new Skill();
        skill.setSkillName(skillRequestDto.getSkillName());
        skill.setUser(user);
        Skill savedSkill = skillRepository.save(skill);

        SkillResponseDto skillResponseDto = new SkillResponseDto();
        skillResponseDto.setSkillName(savedSkill.getSkillName());
        skillResponseDto.setId(savedSkill.getId());
        skillResponseDto.setUser_id(savedSkill.getUser().getId());
        return skillResponseDto;
    }


    }





