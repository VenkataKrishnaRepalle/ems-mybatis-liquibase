package com.learning.emsmybatisliquibase.service.impl;

import com.learning.emsmybatisliquibase.dao.EmployeeDao;
import com.learning.emsmybatisliquibase.dao.EmployeeRoleDao;
import com.learning.emsmybatisliquibase.dto.JwtAuthResponseDto;
import com.learning.emsmybatisliquibase.dto.LoginDto;
import com.learning.emsmybatisliquibase.exception.NotFoundException;
import com.learning.emsmybatisliquibase.security.JwtTokenProvider;
import com.learning.emsmybatisliquibase.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final EmployeeDao employeeDao;

    private final EmployeeRoleDao employeeRoleDao;

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    private final PasswordEncoder passwordEncoder;

    @Override
    public JwtAuthResponseDto login(LoginDto loginDto) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getEmail(),
                loginDto.getPassword()
        ));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generateToken(authentication);

        var employee = employeeDao.getByEmail(loginDto.getEmail());

        if (employee == null) {
            throw new NotFoundException("Employee not found with email" + loginDto.getEmail());
        }

        List<String> roles = new ArrayList<>();
        var employeeRoles = employeeRoleDao.getByEmployeeUuid(employee.getUuid());
        employeeRoles.forEach(role -> roles.add(role.getRole().toString()));

        if (employee.getIsManager().equals(Boolean.TRUE)) {
            roles.add("MANAGER");
        }
        return JwtAuthResponseDto.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .roles(roles)
                .build();
    }
}
