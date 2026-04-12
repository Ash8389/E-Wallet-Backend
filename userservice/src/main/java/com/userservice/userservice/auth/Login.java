package com.userservice.userservice.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.userservice.userservice.dtos.LoginRequest;
import com.userservice.userservice.dtos.UserDetail;
import com.userservice.userservice.jwt.utils.JwtUtils;
import com.userservice.userservice.model.User;
import com.userservice.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class Login {

    private final AuthenticationManager authenticationManager;
    private JwtUtils jwtUtils;
    private final StringRedisTemplate redisTemplate;
    private final UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    Login(AuthenticationManager authenticationManager,
          JwtUtils jwtUtils,
          StringRedisTemplate redisTemplate,
          UserService userService){
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.redisTemplate = redisTemplate;
        this.userService = userService;
    }

    @Operation(summary = "For login user", description = "For login user")
    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest request){
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            UserDetail detail = userService.getUserDetail(request.getEmail());
            return ResponseEntity.ok(jwtUtils.generateToken(detail));
    }
}
