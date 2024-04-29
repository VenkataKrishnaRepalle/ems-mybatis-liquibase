package com.learning.emsmybatisliquibase.service;

import com.learning.emsmybatisliquibase.dto.JwtAuthResponseDto;
import com.learning.emsmybatisliquibase.dto.LoginDto;

public interface AuthService {
    JwtAuthResponseDto login(LoginDto loginDto);
}
