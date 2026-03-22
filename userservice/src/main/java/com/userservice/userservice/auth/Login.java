package com.userservice.userservice.auth;

import com.userservice.userservice.dtos.LoginRequest;
import com.userservice.userservice.jwt.utils.JwtUtils;
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

    Login(AuthenticationManager authenticationManager, JwtUtils jwtUtils){
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
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
