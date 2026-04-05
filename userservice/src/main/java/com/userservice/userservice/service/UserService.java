package com.userservice.userservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.userservice.userservice.dtos.RegisterRequest;
import com.userservice.userservice.dtos.RegisterResponse;
import com.userservice.userservice.dtos.UserDetail;
import com.userservice.userservice.model.User;
import com.userservice.userservice.repository.UserRepo;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {

    private UserRepo userRepo;
    private PasswordEncoder passwordEncoder;
    private StringRedisTemplate redisTemplate;
    private ObjectMapper objectMapper = new ObjectMapper();


    UserService(UserRepo userRepo,
                PasswordEncoder passwordEncoder,
                StringRedisTemplate redisTemplate) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.redisTemplate = redisTemplate;
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

    public UserDetail getUserDetail(String email) {

        String key = "userMail:" + email;

        String jsonUser = redisTemplate.opsForValue().get(key);

        if(jsonUser != null){
            try{
                return objectMapper.readValue(jsonUser, UserDetail.class);
            } catch (Exception e) {
                throw new RuntimeException("Redis Read fail : " + e);
            }
        }

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User Not found"));

        UserDetail response = new UserDetail(
                user.getId(),
                user.getName(),
                user.getEmail()
        );

        try{
            String json = objectMapper.writeValueAsString(response);
            redisTemplate.opsForValue().set(key, json);
        } catch (Exception e) {
            throw new RuntimeException("Redis write fail : " + e);
        }

        return response;
    }
}
