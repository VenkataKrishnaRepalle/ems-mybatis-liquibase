package com.learning.emsmybatisliquibase.service.impl;

import com.learning.emsmybatisliquibase.dao.PasswordDao;
import com.learning.emsmybatisliquibase.dto.JwtAuthResponseDto;
import com.learning.emsmybatisliquibase.dto.LoginDto;
import com.learning.emsmybatisliquibase.dto.SuccessResponseDto;
import com.learning.emsmybatisliquibase.entity.PasswordStatus;
import com.learning.emsmybatisliquibase.entity.ProfileStatus;
import com.learning.emsmybatisliquibase.entity.RoleType;
import com.learning.emsmybatisliquibase.exception.InvalidInputException;
import com.learning.emsmybatisliquibase.security.JwtTokenProvider;
import com.learning.emsmybatisliquibase.service.EmployeeService;
import com.learning.emsmybatisliquibase.service.PasswordService;
import com.learning.emsmybatisliquibase.service.EmployeeRoleService;
import com.learning.emsmybatisliquibase.service.ProfileService;
import com.learning.emsmybatisliquibase.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.learning.emsmybatisliquibase.exception.errorcodes.EmployeeErrorCodes.PASSWORD_NOT_MATCHED;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final EmployeeService employeeService;

    private final PasswordDao passwordDao;

    private final PasswordService passwordService;

    private final EmployeeRoleService employeeRoleService;

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final ProfileService profileService;

    @Override
    public JwtAuthResponseDto login(LoginDto loginDto) {
        var employee = employeeService.getByEmail(loginDto.getEmail());

        var profile = profileService.getByEmployeeUuid(employee.getUuid());
        if (profile.getProfileStatus() == ProfileStatus.PENDING) {
            throw new InvalidInputException("ACCOUNT_NOT_ACTIVATED", "Account not activated, Please set new password");
        } else if (profile.getProfileStatus() == ProfileStatus.INACTIVE) {
            throw new InvalidInputException("NOT_AUTHORIZED_USER", "You're not eligible to access this application");
        }

        var passwords = passwordDao.getByEmployeeUuidAndStatus(employee.getUuid(),
                PasswordStatus.ACTIVE);
        if (passwords.size() != 1) {
            throw new InvalidInputException("ACCOUNT_LOCKED", "Account Locked, Please reset password");
        }

        var password = passwords.get(0);

        if (!passwordEncoder.matches(loginDto.getPassword(), password.getPassword())) {
            password.setNoOfIncorrectEntries(password.getNoOfIncorrectEntries() + 1);
            if (password.getNoOfIncorrectEntries() == 3) {
                password.setStatus(PasswordStatus.LOCKED);
            }
            passwordService.update(password);

            if (password.getNoOfIncorrectEntries() >= 3) {
                throw new InvalidInputException("ACCOUNT_LOCKED", "Your account locked, please reset your password");
            }
            throw new InvalidInputException(PASSWORD_NOT_MATCHED.code(), "Entered Password in Incorrect");
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        String.valueOf(employee.getUuid()),
                        loginDto.getPassword()
                ));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(authentication);
        var roles = employeeRoleService.getRolesByEmployeeUuid(employee.getUuid())
                .stream()
                .map(RoleType::toString)
                .toList();
        return JwtAuthResponseDto.builder()
                .employeeId(employee.getUuid())
                .email(employee.getEmail())
                .accessToken(token)
                .tokenType("Bearer")
                .roles(roles)
                .build();
    }

    @Override
    public SuccessResponseDto verifyEmail(String email) {
        var employee = employeeService.getByEmail(email);
        return SuccessResponseDto.builder()
                .data(employee == null ? null : employee.getUuid().toString())
                .success(employee != null)
                .build();
    }

    public boolean isCurrentUser(final UUID userId) {
        if (userId == null) {
            return false;
        }
        var currentUserId = getCurrentUserId();
        var employeeProfile = profileService.getByEmployeeUuid(userId);
        if (employeeProfile == null || !employeeProfile.getProfileStatus().equals(ProfileStatus.ACTIVE)) {
            return false;
        }
        return userId.equals(currentUserId);
    }

    private UUID getCurrentUserId() {
        return UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    public boolean isEmployeeManager(final UUID userId) {
        var currentUserId = getCurrentUserId();
        var employee = employeeService.getById(userId);
        return employee.getManagerUuid().equals(currentUserId);
    }
}
