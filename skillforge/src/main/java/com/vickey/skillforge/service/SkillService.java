package com.vickey.skillforge.service;

import com.vickey.skillforge.dto.SkillRequestDto;
import com.vickey.skillforge.dto.SkillResponseDto;
import com.vickey.skillforge.entity.Skill;
import com.vickey.skillforge.entity.User;
import com.vickey.skillforge.exception.SkillNotFoundException;
import com.vickey.skillforge.exception.UserNotFoundException;
import com.vickey.skillforge.repository.SkillRepository;
import com.vickey.skillforge.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j

@Service
public class SkillService {

    private final SkillRepository skillRepository;
    private final UserRepository userRepository;

    public SkillService(SkillRepository skillRepository, UserRepository userRepository) {
        this.skillRepository = skillRepository;
        this.userRepository = userRepository;
    }

    public SkillResponseDto addSkill(SkillRequestDto skillRequestDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("user not found with:" + userId));
        Skill skill = new Skill();
        skill.setSkillName(skillRequestDto.getSkillName());
        skill.setUser(user);
        Skill savedSkill = skillRepository.save(skill);
        log.info("Skill added successfully with id: {} for user id: {}",
                savedSkill.getId(), userId);

        SkillResponseDto skillResponseDto = new SkillResponseDto();
        skillResponseDto.setSkillName(savedSkill.getSkillName());
        skillResponseDto.setId(savedSkill.getId());
        skillResponseDto.setUserId(savedSkill.getUser().getId());
        return skillResponseDto;
    }

    public List<SkillResponseDto> getAllSkillsOfUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("user not found  with id:" + userId));
        List<Skill> skills = skillRepository.findByUserId(userId);

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

    public SkillResponseDto getSkillById(Long skillId)
    {
        log.info("Fetching skills  with skillid: {}", skillId);
        Skill skill=skillRepository.findById(skillId).orElseThrow(()-> new SkillNotFoundException("skillnot found with id:"+skillId));
        SkillResponseDto skillResponseDto= new SkillResponseDto();
        skillResponseDto.setId(skill.getId());
        skillResponseDto.setSkillName(skill.getSkillName());
        skillResponseDto.setUserId(skill.getUser().getId());
        return skillResponseDto;
    }

    public SkillResponseDto updateSkillById(Long skillId,SkillRequestDto skillRequestDto)
    {
        Skill skill= skillRepository.findById(skillId).orElseThrow(()-> new SkillNotFoundException("skill not found"+skillId));
        skill.setSkillName(skillRequestDto.getSkillName());
        Skill updatedSkill=skillRepository.save(skill);
        log.info("Skill updated successfully with id: {}", updatedSkill.getId());

        SkillResponseDto skillResponseDto= new SkillResponseDto();
        skillResponseDto.setSkillName(updatedSkill.getSkillName());
        skillResponseDto.setUserId(updatedSkill.getUser().getId());
        skillResponseDto.setId(updatedSkill.getId());
        return skillResponseDto;
    }

    public void deleteSkillById(Long skillId)
    {
        Skill skill= skillRepository.findById(skillId).orElseThrow(()-> new SkillNotFoundException("skill not found with skill id: "+ skillId));

        skillRepository.delete(skill);

        log.info("Skill deleted successfully with id: {}", skillId);
    }

    public List<SkillResponseDto> searchSkillsByName(String skillNameGot)
    {
        List<Skill> skills= skillRepository.findBySkillNameContainingIgnoreCase(skillNameGot);

        List<SkillResponseDto> responseDtos =new ArrayList<>();
        for(Skill skill:skills)
        {
            SkillResponseDto skillResponseDto= new SkillResponseDto();
            skillResponseDto.setSkillName(skill.getSkillName());
            skillResponseDto.setId(skill.getId());
            skillResponseDto.setUserId(skill.getUser().getId());
            responseDtos.add(skillResponseDto);

        }
        return responseDtos;

    }

    //paging
    public Page<SkillResponseDto> getAllSkillsPaginated(int page, int size,String sortBy,String direction)
    {
        if (page < 0) {
            page = 0;
        }

        if (size <= 0) {
            size = 5;
        }


        if (!sortBy.equals("id") &&
                !sortBy.equals("skillName")) {

            sortBy = "id";
        }

        Sort sort;

        if (direction.equalsIgnoreCase("desc")) {
            sort = Sort.by(sortBy).descending();
        } else {
            sort = Sort.by(sortBy).ascending();
        }

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Skill> skills= skillRepository.findAll(pageable);

        return skills.map(skill->{
            SkillResponseDto skillResponseDto = new SkillResponseDto();

            skillResponseDto.setId(skill.getId());
            skillResponseDto.setSkillName(skill.getSkillName());
            skillResponseDto.setUserId(skill.getUser().getId());

            return skillResponseDto;
        });
    }

    public SkillResponseDto createSkill(String email, SkillRequestDto skillRequestDto) {
        User user= userRepository.findByEmail(email).orElseThrow(()-> new UserNotFoundException("not found with this email"+email));
        Skill skill= new Skill();
        skill.setSkillName(skillRequestDto.getSkillName());
        skill.setLevel(skillRequestDto.getLevel());
        skill.setCategory(skillRequestDto.getCategory());
        skill.setUser(user);
        skill.setDescription(skillRequestDto.getDescription());

        Skill savedSkill= skillRepository.save(skill);
        return convertToSkillResponseDto(savedSkill);
    }

    private SkillResponseDto convertToSkillResponseDto(Skill savedSkill) {
        SkillResponseDto skillResponseDto= new SkillResponseDto();

        skillResponseDto.setSkillName(savedSkill.getSkillName());
        skillResponseDto.setId(savedSkill.getId());
        skillResponseDto.setCategory(savedSkill.getCategory());
        skillResponseDto.setLevel(savedSkill.getLevel());
        skillResponseDto.setDescription(savedSkill.getDescription());

        skillResponseDto.setUserId(savedSkill.getUser().getId());
        skillResponseDto.setUserName(savedSkill.getUser().getName());
        return  skillResponseDto;

    }
}





