package com.userservice.userservice.auth;

import com.userservice.userservice.dtos.LoginRequest;
import com.userservice.userservice.dtos.UserDetail;
import com.userservice.userservice.jwt.utils.JwtUtils;
import com.userservice.userservice.service.UserService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/users")
public class Login {

    private final AuthenticationManager authenticationManager;
    private JwtUtils jwtUtils;
    private final StringRedisTemplate redisTemplate;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    Login(AuthenticationManager authenticationManager,
          JwtUtils jwtUtils,
          StringRedisTemplate redisTemplate,
          UserService userService,
          ObjectMapper objectMapper){
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.redisTemplate = redisTemplate;
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request){

        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            return ResponseEntity.ok(jwtUtils.generateToken(request));

        }catch (Exception e){
            return ResponseEntity.status(401).body("Invalid Credentials");
        }
    }
}
