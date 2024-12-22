package com.learning.emsmybatisliquibase.service;

import com.learning.emsmybatisliquibase.dto.*;

public interface AuthService {
    JwtAuthResponseDto login(LoginDto loginDto);

    SuccessResponseDto verifyEmail(String email);
}
