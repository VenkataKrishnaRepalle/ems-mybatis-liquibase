package com.learning.emsmybatisliquibase.service;

import com.learning.emsmybatisliquibase.dto.JwtAuthResponseDto;
import com.learning.emsmybatisliquibase.dto.LoginDto;

import java.util.UUID;


public interface AuthService {
    JwtAuthResponseDto login(LoginDto loginDto);

    UUID verifyEmail(String email);
}
