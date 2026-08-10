package com.vickey.skillforge.controller;


import com.vickey.skillforge.dto.LoginRequestDto;
import com.vickey.skillforge.dto.UserRequestDto;
import com.vickey.skillforge.dto.UserResponseDto;
import com.vickey.skillforge.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
@Tag(name = "User APIs", description = "APIs for user registration and user management")

@RestController
@RequestMapping("/api/users")
public class UserController {


    private final UserService userService;
    public UserController(UserService userService)
    {
        this.userService=userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@Valid @RequestBody UserRequestDto userRequestDto)
    {
        UserResponseDto userResponseDto= userService.registerUser(userRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDto);
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
    @PutMapping("/{userId}")
    public ResponseEntity<UserResponseDto> upddateUserById(@PathVariable Long userId, @Valid @RequestBody UserRequestDto userRequestDto)
    {
        UserResponseDto updateUser =userService.updateUser(userId,userRequestDto);
        return ResponseEntity.ok(updateUser);
    }

    @Operation(summary = "Delete specific user by ID")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long userId)
    {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    //login user
    @PostMapping("/login")
    public ResponseEntity<UserResponseDto> loginUser(@Valid @RequestBody  LoginRequestDto loginRequestDto)
    {
        UserResponseDto userResponseDto= userService.loginUser(loginRequestDto);
        return ResponseEntity.ok(userResponseDto);
    }

}
