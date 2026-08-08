package com.vickey.skillForge.service;

import com.vickey.skillForge.dto.SkillRequestDto;
import com.vickey.skillForge.dto.SkillResponseDto;
import com.vickey.skillForge.entity.Skill;
import com.vickey.skillForge.entity.User;
import com.vickey.skillForge.exception.SkillNotFoundException;
import com.vickey.skillForge.exception.UserNotFoundException;
import com.vickey.skillForge.repository.SkillRepository;
import com.vickey.skillForge.repository.UserRepository;
import org.hibernate.query.SortDirection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
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

    public SkillResponseDto updateSkillById(Long id,SkillRequestDto skillRequestDto)
    {
        Skill skill= skillRepository.findById(id).orElseThrow(()-> new SkillNotFoundException("skill not found"+id));
        skill.setSkillName(skillRequestDto.getSkillName());
        Skill updatedSkill=skillRepository.save(skill);

        SkillResponseDto skillResponseDto= new SkillResponseDto();
        skillResponseDto.setSkillName(updatedSkill.getSkillName());
        skillResponseDto.setUserId(updatedSkill.getUser().getId());
        skillResponseDto.setId(updatedSkill.getId());
        return skillResponseDto;
    }

    public void deleteSkillById(Long id)
    {
        Skill skill= skillRepository.findById(id).orElseThrow(()-> new SkillNotFoundException("skill not found with skill id: "+ id));

        skillRepository.delete(skill);
    }

    public List<SkillResponseDto> findByskillname(String skillNameGot)
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
}





