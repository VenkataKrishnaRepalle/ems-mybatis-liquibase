package com.learning.emsmybatisliquibase.service;

import com.learning.emsmybatisliquibase.dto.JwtAuthResponseDto;
import com.learning.emsmybatisliquibase.dto.LoginDto;
import com.learning.emsmybatisliquibase.dto.SuccessResponseDto;

import java.util.UUID;


public interface AuthService {
    JwtAuthResponseDto login(LoginDto loginDto);

    UUID verifyEmail(String email);
}
