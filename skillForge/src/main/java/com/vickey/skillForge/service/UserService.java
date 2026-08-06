package com.vickey.skillForge.service;



import com.vickey.skillForge.dto.SkillResponseDto;
import com.vickey.skillForge.dto.UserRequestDto;
import com.vickey.skillForge.dto.UserResponseDto;
import com.vickey.skillForge.entity.Skill;
import com.vickey.skillForge.entity.User;
import com.vickey.skillForge.exception.EmailAlreadyExistsException;
import com.vickey.skillForge.exception.UserNotFoundException;
import com.vickey.skillForge.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

  private final  UserRepository userRepository;

  public UserService(UserRepository userRepository)
  {
      this.userRepository=userRepository;
  }

  //creation of conversion of dto to entites
    public UserResponseDto registerUser(UserRequestDto userRequestDto)
    {
        if (userRepository.existsByEmail(userRequestDto.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }
        User user= convertToUser(userRequestDto);
        User savedUser= userRepository.save(user);
        return convertToResponseDto(savedUser);
    }
    private User convertToUser(UserRequestDto userRequestDto)
    {
        User user=new User();
        user.setName(userRequestDto.getName());
        user.setEmail(userRequestDto.getEmail());
        user.setPassword(userRequestDto.getPassword());
        return user;
    }
    private UserResponseDto convertToResponseDto(User user) {

        UserResponseDto responseDTO = new UserResponseDto();

        responseDTO.setId(user.getId());
        responseDTO.setName(user.getName());
        responseDTO.setEmail(user.getEmail());

        List<SkillResponseDto> skillDtos = new ArrayList<>();
        for (Skill skill : user.getSkills()) {

            SkillResponseDto skillDto = new SkillResponseDto();

            skillDto.setId(skill.getId());
            skillDto.setSkillName(skill.getSkillName());
            skillDto.setUserId(user.getId());

            skillDtos.add(skillDto);
        }

        responseDTO.setSkills(skillDtos);

        return responseDTO;
    }

    //finding by id
    public UserResponseDto findId(Long id)
    {
        User user= userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return convertToResponseDto(user);
    }
    public List<UserResponseDto> getAllUsers() {

        List<User> users = userRepository.findAll();

        return users.stream()
                .map(this::convertToResponseDto)
                .toList();
    }
    //updating user
    public UserResponseDto updateUser(Long id, UserRequestDto userRequestDto)
    {
        User user= userRepository.findById(id).orElseThrow(()-> new UserNotFoundException("User not found with id: " + id));
        user.setName(userRequestDto.getName());
        user.setEmail(userRequestDto.getEmail());
        user.setPassword(userRequestDto.getPassword());
        User updatedUser=userRepository.save(user);
        return convertToResponseDto(updatedUser);
    }

    //deleting users by id
    public void deleteUser(Long id)
    {
        User user=userRepository.findById(id).orElseThrow(()->new UserNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
    }


}
