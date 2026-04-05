package com.userservice.userservice.controller;

import com.userservice.userservice.dtos.RegisterRequest;
import com.userservice.userservice.dtos.RegisterResponse;
import com.userservice.userservice.dtos.UserDetail;
import com.userservice.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.context.annotation.Description;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private UserService userService;

    UserController(UserService userService){
        this.userService = userService;
    }

    @Operation(summary = "For register user", description = "For register user")
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest registerRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(registerRequest));
    }

    @Operation(summary = "Get user details", description = "Get user details by using there email")
    @GetMapping
    public ResponseEntity<UserDetail> getUser(@RequestHeader("X-User-Email") String email) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserDetail(email));
    }

}
