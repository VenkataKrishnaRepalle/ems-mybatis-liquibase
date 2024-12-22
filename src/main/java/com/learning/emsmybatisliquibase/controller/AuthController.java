package com.learning.emsmybatisliquibase.controller;

import com.learning.emsmybatisliquibase.dto.*;
import com.learning.emsmybatisliquibase.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponseDto> login(@RequestBody LoginDto loginDto) {
        return new ResponseEntity<>(authService.login(loginDto), HttpStatus.OK);
    }

    @PostMapping("/verifyEmail")
    public ResponseEntity<SuccessResponseDto> verifyEmail(@RequestParam(name = "email") String email) {
        return new ResponseEntity<>(authService.verifyEmail(email), HttpStatus.OK);
    }
}
