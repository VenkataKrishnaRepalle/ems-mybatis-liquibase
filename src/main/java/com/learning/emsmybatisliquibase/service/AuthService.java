package com.learning.emsmybatisliquibase.service;

import com.learning.emsmybatisliquibase.dto.*;

import java.util.Map;
import java.util.UUID;

public interface AuthService {
    JwtAuthResponseDto login(LoginDto loginDto);

    SuccessResponseDto verifyEmail(String email);

    Map<String, Boolean> validateToken(UUID employeeId, String token);
}
