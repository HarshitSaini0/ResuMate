package com.example.air_resume_mate.service;

import com.example.air_resume_mate.model.User;
import com.example.air_resume_mate.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // <-- ADD THIS

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) { // <-- ADD TO CONSTRUCTOR
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder; // <-- ADD THIS
    }

    public User createUser(User user) {
        // Hash the password before saving
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword); // <-- ADD THIS
        return userRepository.save(user);
    }
}