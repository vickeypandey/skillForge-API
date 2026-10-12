package com.vickey.skillforge.controller;


import com.vickey.skillforge.dto.*;
import com.vickey.skillforge.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
@Tag(name = "User APIs", description = "APIs for user registration and user management")
@SecurityRequirement(name = "bearerAuth")

@RestController
@RequestMapping("/api/v1/users")
public class UserController {


    private final UserService userService;
    public UserController(UserService userService)
    {
        this.userService=userService;
    }

    @Operation(summary = "Get users by ID")
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getbyId(@PathVariable Long userId)
    {
        UserResponseDto userResponseDto= userService.getUserById(userId);
        return ResponseEntity.ok(userResponseDto);
    }

    @Operation(summary = "Get all users")
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {

        List<UserResponseDto> users =
                userService.getAllUsers();

        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Update user  by ID")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{userId}")
    public ResponseEntity<UserResponseDto> upddateUserById(@PathVariable Long userId, @Valid @RequestBody UpdateUserRequestDto userRequestDto)
    {
        UserResponseDto updateUser =userService.updateUser(userId,userRequestDto);
        return ResponseEntity.ok(updateUser);
    }

    @Operation(summary = "Delete specific user by ID")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long userId)
    {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUser(
            Authentication authentication) {

        UserResponseDto user =
                userService.getCurrentUser(authentication.getName());

        return ResponseEntity.ok(user);
    }
    //updating self data
    @PutMapping("/me")
    public ResponseEntity<UserResponseDto> updateCurrentUser(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequestDto updateProfileRequestDto) {

        UserResponseDto updatedUser =
                userService.updateCurrentUser(
                        authentication.getName(),
                        updateProfileRequestDto
                );

        return ResponseEntity.ok(updatedUser);
    }

    @PostMapping("/me/change-password")
    public ResponseEntity<Void> changePassword(Authentication authentication,
                                               @Valid @RequestBody ChangePasswordRequestDto request) {
        userService.changePassword(authentication.getName(), request);
        return ResponseEntity.noContent().build();
    }

}
