package com.example.air_resume_mate.controller;

import com.example.air_resume_mate.model.User;
import com.example.air_resume_mate.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.air_resume_mate.dto.LoginRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import com.example.air_resume_mate.dto.JwtResponse;
import com.example.air_resume_mate.service.JwtService;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService; // <-- ADD THIS

    @Autowired
    public UserController(UserService userService, AuthenticationManager authenticationManager, JwtService jwtService) { // <-- ADD TO CONSTRUCTOR
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService; // <-- ADD THIS
    }

    @PostMapping("/register")
    public User registerUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    // ADD THE METHOD BELOW
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) { // <-- Change return type
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtService.generateToken(authentication); // <-- GENERATE THE TOKEN

        return ResponseEntity.ok(new JwtResponse(jwt)); // <-- RETURN THE TOKEN
    }
}