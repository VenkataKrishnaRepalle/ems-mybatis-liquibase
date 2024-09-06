package com.learning.emsmybatisliquibase.service.impl;

import com.learning.emsmybatisliquibase.dao.EmployeeDao;
import com.learning.emsmybatisliquibase.dao.EmployeeRoleDao;
import com.learning.emsmybatisliquibase.dao.PasswordDao;
import com.learning.emsmybatisliquibase.dao.ProfileDao;
import com.learning.emsmybatisliquibase.dto.JwtAuthResponseDto;
import com.learning.emsmybatisliquibase.dto.LoginDto;
import com.learning.emsmybatisliquibase.entity.PasswordStatus;
import com.learning.emsmybatisliquibase.entity.ProfileStatus;
import com.learning.emsmybatisliquibase.exception.IntegrityException;
import com.learning.emsmybatisliquibase.exception.InvalidInputException;
import com.learning.emsmybatisliquibase.exception.NotFoundException;
import com.learning.emsmybatisliquibase.security.JwtTokenProvider;
import com.learning.emsmybatisliquibase.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.learning.emsmybatisliquibase.exception.errorcodes.EmployeeErrorCodes.EMPLOYEE_NOT_FOUND;
import static com.learning.emsmybatisliquibase.exception.errorcodes.EmployeeErrorCodes.PASSWORD_NOT_MATCHED;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final EmployeeDao employeeDao;

    private final PasswordDao passwordDao;

    private final EmployeeRoleDao employeeRoleDao;

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    private final PasswordEncoder passwordEncoder;

    private final ProfileDao profileDao;

    @Override
    public JwtAuthResponseDto login(LoginDto loginDto) {
        var employee = employeeDao.getByEmail(loginDto.getEmail());
        if (employee == null) {
            throw new NotFoundException(EMPLOYEE_NOT_FOUND.code(), "Employee not found with email: " + loginDto.getEmail());
        }

        var profile = profileDao.get(employee.getUuid());
        if (profile.getProfileStatus() == ProfileStatus.PENDING) {
            throw new InvalidInputException("ACCOUNT_NOT_ACTIVATED", "Account not activated, Please set new password");
        } else if (profile.getProfileStatus() == ProfileStatus.INACTIVE) {
            throw new InvalidInputException("NOT_AUTHORIZED_USER", "You're not eligible to access this application");
        }

        var passwords = passwordDao.getByEmployeeUuidAndStatus(employee.getUuid(), PasswordStatus.ACTIVE);
        if (passwords.size() != 1) {
            throw new InvalidInputException("INVALID_LOGIN", "Account Locked, Please reset password");
        }

        var password = passwords.get(0);

        if (!passwordEncoder.matches(loginDto.getPassword(), password.getPassword())) {
            password.setNoOfIncorrectEntries(password.getNoOfIncorrectEntries() + 1);
            if (password.getNoOfIncorrectEntries() == 3) {
                password.setStatus(PasswordStatus.LOCKED);
            }
            try {
                if (0 == passwordDao.update(password)) {
                    throw new IntegrityException("PASSWORD_UPDATE_FAILED", "Password not updated");
                }
            } catch (DataIntegrityViolationException exception) {
                throw new IntegrityException("PASSWORD_UPDATE_FAILED", "Password not updated");
            }

            if (password.getNoOfIncorrectEntries() >= 3) {
                throw new InvalidInputException("ACCOUNT_LOCKED", "Your account locked, please reset your password");
            }
            throw new InvalidInputException(PASSWORD_NOT_MATCHED.code(), "Entered Password in Incorrect");
        }
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                String.valueOf(employee.getUuid()),
                loginDto.getPassword()
        ));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(authentication);
        List<String> roles = new ArrayList<>();
        var employeeRoles = employeeRoleDao.getByEmployeeUuid(employee.getUuid());
        employeeRoles.forEach(role -> roles.add(role.getRole().toString()));

        if (employee.getIsManager().equals(Boolean.TRUE)) {
            roles.add("MANAGER");
        }
        roles.add("EMPLOYEE");
        return JwtAuthResponseDto.builder()
                .employeeId(employee.getUuid())
                .accessToken(token)
                .tokenType("Bearer")
                .roles(roles)
                .build();
    }

    @Override
    public UUID verifyEmail(String email) {
        var employee = employeeDao.getByEmail(email.trim());
        if (employee == null) {
            throw new NotFoundException(EMPLOYEE_NOT_FOUND.code(), "Employee not found with email: " + email);
        }
        return employee.getUuid();
    }

    public Boolean isCurrentUser(final UUID userId) {
        if (userId == null) {
            return false;
        }
        var currentUserId = getCurrentUserId();
        var employeeProfile = profileDao.get(userId);
        if (employeeProfile == null || !employeeProfile.getProfileStatus().equals(ProfileStatus.ACTIVE)) {
            return false;
        }
        return userId.equals(currentUserId);
    }

    private UUID getCurrentUserId() {
        return UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    public Boolean isEmployeeManager(final UUID userId) {
        var currentUserId = getCurrentUserId();
        var employee = employeeDao.get(userId);
        return employee.getManagerUuid().equals(currentUserId);
    }
}
