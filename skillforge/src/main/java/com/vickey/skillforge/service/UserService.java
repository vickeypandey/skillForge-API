package com.vickey.skillforge.service;
import com.vickey.skillforge.dto.*;
import com.vickey.skillforge.entity.Skill;
import com.vickey.skillforge.entity.User;
import com.vickey.skillforge.exception.EmailAlreadyExistsException;
import com.vickey.skillforge.exception.InvalidCredentialsException;
import com.vickey.skillforge.exception.UserNotFoundException;
import com.vickey.skillforge.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import com.vickey.skillforge.service.JwtService;


@Slf4j

@Service
public class UserService {

  private final  UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  public UserService(UserRepository userRepository,PasswordEncoder passwordEncoder,JwtService jwtService)
  {
      this.userRepository=userRepository;
      this.passwordEncoder=passwordEncoder;
      this.jwtService=jwtService;
  }

  //creation of conversion of dto to entites
    public UserResponseDto registerUser(UserRequestDto userRequestDto)
    {
        if (userRepository.existsByEmail(userRequestDto.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }
        User user= convertToUser(userRequestDto);
        User savedUser= userRepository.save(user);
        log.info("User registered successfully with id: {}", savedUser.getId());
        return convertToResponseDto(savedUser);
    }
    private User convertToUser(UserRequestDto userRequestDto)
    {
        User user=new User();
        user.setName(userRequestDto.getName());
        user.setEmail(userRequestDto.getEmail());
        user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
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
    public UserResponseDto getUserById(Long userId)
    {
        log.info("Fetching user with id: {}", userId);
        User user= userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        return convertToResponseDto(user);
    }
    public List<UserResponseDto> getAllUsers() {

        List<User> users = userRepository.findAll();

        return users.stream()
                .map(this::convertToResponseDto)
                .toList();
    }
    //updating user
    public UserResponseDto updateUser(Long userId, UserRequestDto userRequestDto)
    {
        User user= userRepository.findById(userId).orElseThrow(()-> new UserNotFoundException("User not found with id: " + userId));
        user.setName(userRequestDto.getName());
        user.setEmail(userRequestDto.getEmail());
        user.setPassword(userRequestDto.getPassword());
        User updatedUser=userRepository.save(user);
        return convertToResponseDto(updatedUser);
    }

    //deleting users by id
    public void deleteUser(Long userId)
    {
        User user=userRepository.findById(userId).orElseThrow(()->new UserNotFoundException("User not found with id: " + userId));
        userRepository.delete(user);
    }


    //login method
    public LoginResponseDto loginUser(LoginRequestDto loginRequestDto)
    {
        User user= userRepository.findByEmail(loginRequestDto.getEmail()).orElseThrow(()-> new UserNotFoundException("user not found with this email."));

        boolean passwordMatches= passwordEncoder.matches(loginRequestDto.getPassword(),user.getPassword());

        if(!passwordMatches) {
            throw new InvalidCredentialsException("wrong email or password");
        }
       String token= jwtService.generateToken(user.getEmail());
        return new LoginResponseDto(token);
    }

}
