package com.vickey.skillforge.service;

import com.vickey.skillforge.dto.SkillRequestDto;
import com.vickey.skillforge.dto.SkillResponseDto;
import com.vickey.skillforge.entity.Skill;
import com.vickey.skillforge.entity.SkillCategory;
import com.vickey.skillforge.entity.SkillLevel;
import com.vickey.skillforge.entity.User;
import com.vickey.skillforge.enums.Role;
import com.vickey.skillforge.exception.DuplicateSkillException;
import com.vickey.skillforge.exception.SkillNotFoundException;
import com.vickey.skillforge.exception.UserNotFoundException;
import com.vickey.skillforge.exception.ForbiddenOperationException;
import com.vickey.skillforge.repository.SkillRepository;
import com.vickey.skillforge.repository.UserRepository;
import jakarta.validation.Valid;
import org.jspecify.annotations.Nullable;
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

    public Page<SkillResponseDto> searchSkillsByName(String skillNameGot, Pageable pageable)
    {
        return skillRepository.findBySkillNameContainingIgnoreCase(skillNameGot, pageable)
                .map(this::convertToSkillResponseDto);
    }

    //paging
    public Page<SkillResponseDto> getAllSkillsPaginated(int page, int size,String sortBy,String direction)
    {
        if (page < 0) {
            page = 0;
        }

        size = Math.max(1, Math.min(size, 100));


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
        if (skillRepository
                .existsByUserAndSkillNameIgnoreCase(
                        user,
                        skillRequestDto.getSkillName())) {
            throw new DuplicateSkillException(
                    "You already added this skill");
        }
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

    public Page<SkillResponseDto> getByCategory(SkillCategory category, Pageable pageable) {
        return skillRepository.findByCategory(category, pageable).map(this::convertToSkillResponseDto);
    }
    public Page<SkillResponseDto> getByLevel(SkillLevel skillLevel, Pageable pageable)
    {
        return skillRepository.findByLevel(skillLevel, pageable).map(this::convertToSkillResponseDto);
    }


    public List<SkillResponseDto> getMySkills(String email) {

        // 1. Find the currently logged-in user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        // 2. Find all skills belonging to that user
        List<Skill> skills = skillRepository.findByUser(user);

        // 3. Convert Skill entities into SkillResponseDto objects
        return skills.stream()
                .map(this::convertToSkillResponseDto)
                .toList();
    }


    public SkillResponseDto updateSkill(
            Long skillId,
            String email,
            SkillRequestDto requestDto) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() ->
                        new SkillNotFoundException(
                                "Skill not found"
                        )
                );
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found"
                        )
                );
        boolean isOwner =
                skill.getUser().getId()
                        .equals(currentUser.getId());
        boolean isAdmin =
                currentUser.getRole() == Role.ADMIN;
        if (!isOwner && !isAdmin) {
            throw new ForbiddenOperationException("You cannot update this skill");
        }
        skill.setSkillName(requestDto.getSkillName());
        skill.setDescription(requestDto.getDescription());
        skill.setCategory(requestDto.getCategory());
        skill.setLevel(requestDto.getLevel());
        return convertToSkillResponseDto(
                skillRepository.save(skill)
        );
    }

    public void deleteSkill(Long skillId, String email) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new SkillNotFoundException("Skill not found"));
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        boolean isOwner = skill.getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        if (!isOwner && !isAdmin) {
            throw new ForbiddenOperationException("You cannot delete this skill");
        }
        skillRepository.delete(skill);
    }
}





