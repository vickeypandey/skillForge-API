package com.vickey.skillForge.service;

import com.vickey.skillForge.dto.SkillRequestDto;
import com.vickey.skillForge.dto.SkillResponseDto;
import com.vickey.skillForge.entity.Skill;
import com.vickey.skillForge.entity.User;
import com.vickey.skillForge.exception.SkillNotFoundException;
import com.vickey.skillForge.exception.UserNotFoundException;
import com.vickey.skillForge.repository.SkillRepository;
import com.vickey.skillForge.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SkillService {

    private final SkillRepository skillRepository;
    private final UserRepository userRepository;

    public SkillService(SkillRepository skillRepository, UserRepository userRepository) {
        this.skillRepository = skillRepository;
        this.userRepository = userRepository;
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
        skillResponseDto.setUserId(savedSkill.getUser().getId());
        return skillResponseDto;
    }

    public List<SkillResponseDto> getAllSkillsOfUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("user not found  with id:" + id));
        List<Skill> skills = skillRepository.findByUserId(id);

        List<SkillResponseDto> skillResponseDtos = new ArrayList<>();

        for (Skill skill : skills) {
            SkillResponseDto skillResponseDto = new SkillResponseDto();
            skillResponseDto.setId(skill.getId());
            skillResponseDto.setSkillName(skill.getSkillName());
            skillResponseDto.setUserId(user.getId());
            skillResponseDtos.add(skillResponseDto);
        }
        return skillResponseDtos;
    }

    public SkillResponseDto getSkillById(Long id)
    {
        Skill skill=skillRepository.findById(id).orElseThrow(()-> new SkillNotFoundException("skillnot found with id:"+id));
        SkillResponseDto skillResponseDto= new SkillResponseDto();
        skillResponseDto.setId(skill.getId());
        skillResponseDto.setSkillName(skill.getSkillName());
        skillResponseDto.setUserId(skill.getUser().getId());
        return skillResponseDto;
    }



}





