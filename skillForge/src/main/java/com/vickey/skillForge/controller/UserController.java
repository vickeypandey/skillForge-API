package com.vickey.skillForge.controller;


import com.vickey.skillForge.dto.UserRequestDto;
import com.vickey.skillForge.dto.UserResponseDto;
import com.vickey.skillForge.entity.User;
import com.vickey.skillForge.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getbyId(@PathVariable Long id)
    {
        UserResponseDto userResponseDto= userService.findId(id);
        return ResponseEntity.ok(userResponseDto);
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {

        List<UserResponseDto> users =
                userService.getAllUsers();

        return ResponseEntity.ok(users);
    }

}
