package com.userservice.userservice.service;

import com.userservice.userservice.dtos.RegisterRequest;
import com.userservice.userservice.dtos.RegisterResponse;
import com.userservice.userservice.model.User;
import com.userservice.userservice.repository.UserRepo;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {

    private UserRepo userRepo;

    private PasswordEncoder passwordEncoder;


    UserService(UserRepo userRepo, PasswordEncoder passwordEncoder){
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public RegisterResponse register(RegisterRequest registerUser){
        User user = new User();

        user.setName(registerUser.getName());
        user.setEmail(registerUser.getEmail());
        user.setPassword(passwordEncoder.encode(registerUser.getPassword()));
        user.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepo.save(user);

        RegisterResponse registerResponse = new RegisterResponse();
        registerResponse.setName(savedUser.getName());
        registerResponse.setEmail(savedUser.getEmail());
        registerResponse.setCreatedAt(savedUser.getCreatedAt());

        return registerResponse;
    }
}
