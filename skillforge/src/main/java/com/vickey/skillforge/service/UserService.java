package com.vickey.skillforge.service;
import com.vickey.skillforge.dto.*;
import com.vickey.skillforge.entity.RefreshToken;
import com.vickey.skillforge.entity.User;
import com.vickey.skillforge.enums.Role;
import com.vickey.skillforge.exception.EmailAlreadyExistsException;
import com.vickey.skillforge.exception.InvalidCredentialsException;
import com.vickey.skillforge.exception.UserNotFoundException;
import com.vickey.skillforge.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import com.vickey.skillforge.service.JwtService;


@Slf4j

@Service
public class UserService {

  private final  UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;


    public UserService(UserRepository userRepository,PasswordEncoder passwordEncoder,JwtService jwtService,RefreshTokenService refreshTokenService)
  {
      this.userRepository=userRepository;
      this.passwordEncoder=passwordEncoder;
      this.jwtService=jwtService;
      this.refreshTokenService=refreshTokenService;
  }

  //creation of conversion of dto to entites
    public UserResponseDto registerUser(UserRequestDto userRequestDto)
    {
        String normalizedEmail = userRequestDto.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(normalizedEmail)) {
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
        user.setEmail(userRequestDto.getEmail().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        user.setRole(Role.USER);
        return user;
    }
    private UserResponseDto convertToResponseDto(User user) {

        UserResponseDto responseDTO = new UserResponseDto();

        responseDTO.setId(user.getId());
        responseDTO.setName(user.getName());
        responseDTO.setEmail(user.getEmail());

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
    public UserResponseDto updateUser(Long userId, UpdateUserRequestDto userRequestDto)
    {
        User user= userRepository.findById(userId).orElseThrow(()-> new UserNotFoundException("User not found with id: " + userId));
        String normalizedEmail = userRequestDto.getEmail().trim().toLowerCase();
        if (!user.getEmail().equalsIgnoreCase(normalizedEmail)
                && userRepository.existsByEmail(normalizedEmail)) {
            throw new EmailAlreadyExistsException("Email already exists");
        }
        user.setName(userRequestDto.getName());
        user.setEmail(normalizedEmail);
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
        User user = userRepository.findByEmail(loginRequestDto.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        boolean passwordMatches= passwordEncoder.matches(loginRequestDto.getPassword(),user.getPassword());

        if(!passwordMatches) {
            throw new InvalidCredentialsException("Invalid email or password");
        }
        String accessToken = jwtService.generateToken(user.getEmail(), user.getRole().name());
        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(user);
        return new LoginResponseDto(
                accessToken,
                refreshToken.getToken()
        );
    }


    //me implementation

    public UserResponseDto getCurrentUser(String email) {
        User user= userRepository.findByEmail(email).orElseThrow(()-> new UserNotFoundException("user not found "));
        return convertToResponseDto(user);
    }



    //self update
    public UserResponseDto updateCurrentUser(
            String email,
            UpdateProfileRequestDto updateProfileRequestDto) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with email: " + email
                        )
                );

        user.setName(updateProfileRequestDto.getName());


        User updatedUser = userRepository.save(user);

        return convertToResponseDto(updatedUser);
    }

    public void changePassword(String email, ChangePasswordRequestDto request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        refreshTokenService.revokeForUser(user);
    }

    public LoginResponseDto refreshAccessToken(
            RefreshTokenRequestDto requestDto) {
        RefreshToken refreshToken =
                refreshTokenService.rotate(
                        requestDto.getRefreshToken()
                );
        User user = refreshToken.getUser();
        String newAccessToken = jwtService.generateToken(user.getEmail(), user.getRole().name());
        return new LoginResponseDto(
                newAccessToken,
                refreshToken.getToken()
        );
    }

    public void logout(RefreshTokenRequestDto requestDto) {
        refreshTokenService.revoke(requestDto.getRefreshToken());
    }
}
