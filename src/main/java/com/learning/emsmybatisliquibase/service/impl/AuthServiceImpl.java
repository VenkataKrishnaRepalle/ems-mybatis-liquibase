package com.learning.emsmybatisliquibase.service.impl;

import com.learning.emsmybatisliquibase.dao.EmployeeSessionDao;
import com.learning.emsmybatisliquibase.dao.PasswordDao;
import com.learning.emsmybatisliquibase.dto.JwtAuthResponseDto;
import com.learning.emsmybatisliquibase.dto.LoginDto;
import com.learning.emsmybatisliquibase.dto.SuccessResponseDto;
import com.learning.emsmybatisliquibase.dto.pagination.RequestQuery;
import com.learning.emsmybatisliquibase.entity.*;
import com.learning.emsmybatisliquibase.entity.enums.PasswordStatus;
import com.learning.emsmybatisliquibase.entity.enums.ProfileStatus;
import com.learning.emsmybatisliquibase.entity.enums.RoleType;
import com.learning.emsmybatisliquibase.exception.FoundException;
import com.learning.emsmybatisliquibase.exception.IntegrityException;
import com.learning.emsmybatisliquibase.exception.InvalidInputException;
import com.learning.emsmybatisliquibase.security.JwtTokenProvider;
import com.learning.emsmybatisliquibase.service.EmployeeService;
import com.learning.emsmybatisliquibase.service.PasswordService;
import com.learning.emsmybatisliquibase.service.EmployeeRoleService;
import com.learning.emsmybatisliquibase.service.ProfileService;
import com.learning.emsmybatisliquibase.service.AuthService;
import com.learning.emsmybatisliquibase.utils.UtilityService;
import eu.bitwalker.useragentutils.UserAgent;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

import static com.learning.emsmybatisliquibase.exception.errorcodes.EmployeeErrorCodes.PASSWORD_NOT_MATCHED;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final EmployeeService employeeService;

    private final PasswordDao passwordDao;

    private final PasswordService passwordService;

    private final EmployeeRoleService employeeRoleService;

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final ProfileService profileService;

    private final EmployeeSessionDao employeeSessionDao;

    private final WebClient webClient;

    @Value("${maximum.login.count}")
    Integer MAX_LOGIN_COUNT;

    @Value("${api.location.key}")
    String key;

    @Override
    @Transactional
    public JwtAuthResponseDto login(LoginDto loginDto, HttpServletRequest request) {
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

        var sessions = employeeSessionDao.getByEmployeeUuid(employee.getUuid());
        if (sessions.size() >= MAX_LOGIN_COUNT) {
            throw new FoundException("MAX_LOGIN_ATTEMPT_REACHED", "Max login attempts of " + MAX_LOGIN_COUNT +
                    " reached to your account");
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        String.valueOf(employee.getUuid()),
                        loginDto.getPassword()
                ));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(authentication);

        saveSession(request, employee, loginDto.getRequestQuery(), token);

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

    private void saveSession(HttpServletRequest request, Employee employee, RequestQuery requestQuery, String token) {
        var userAgentString = request.getHeader("User-Agent");
        var userAgent = UserAgent.parseUserAgentString(userAgentString);
        var browser = userAgent.getBrowser();
        var os = userAgent.getOperatingSystem();
        var geoLocations = UtilityService.getLocationInfo(requestQuery);
        var longitude = geoLocations.get("longitude");
        var latitude = geoLocations.get("latitude");
        var location = getLocation(longitude, latitude);
        var platform = UtilityService.getPlatform(requestQuery);
        EmployeeSession session = EmployeeSession.builder()
                .uuid(UUID.randomUUID())
                .employeeUuid(employee.getUuid())
                .token(token)
                .longitude(longitude)
                .latitude(latitude)
                .browserName(browser.getName())
                .platform(platform)
                .osName(os.getName())
                .isActive(true)
                .location(UtilityService.extractAddressInfo(location))
                .loginTime(Instant.now())
                .build();
        try {
            if (0 == employeeSessionDao.insert(session)) {
                throw new IntegrityException("EMPLOYEE_SESSION_NOT_INSERTED",
                        "Employee Session not created for employee : " + employee.getUuid());
            }
        } catch (DataIntegrityViolationException exception) {
            throw new IntegrityException("EMPLOYEE_SESSION_NOT_INSERTED", exception.getCause().getMessage());
        }
    }

    @Override
    public SuccessResponseDto verifyEmail(String email) {
        var employee = employeeService.getByEmail(email);
        return SuccessResponseDto.builder()
                .data(employee == null ? null : employee.getUuid().toString())
                .success(employee != null)
                .build();
    }

    @Override
    public Map<String, Boolean> validateToken(UUID employeeId, String token) {
        if (!StringUtils.hasText(token) && !token.startsWith("Bearer ")) {
            return Map.of("TOKEN_NOT_PROVIDED", true);
        }
        token = token.substring(7);
        var isValid = jwtTokenProvider.validateToken(token);
        if (isValid && !employeeId.toString().equals(jwtTokenProvider.getUsername(token))) {
            isValid = false;
        }
        return Map.of("expired", !isValid);
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

    private RequestQuery getLocation(String longitude, String latitude) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("")
                        .queryParam("q", latitude + "+" + longitude)
                        .queryParam("key", key)
                        .build())
                .retrieve()
                .bodyToMono(RequestQuery.class)
                .block();
    }

}
