package com.learning.emsmybatisliquibase.service.impl;

import com.learning.emsmybatisliquibase.dao.PasswordDao;
import com.learning.emsmybatisliquibase.dto.JwtAuthResponseDto;
import com.learning.emsmybatisliquibase.dto.LoginDto;
import com.learning.emsmybatisliquibase.entity.*;
import com.learning.emsmybatisliquibase.exception.InvalidInputException;
import com.learning.emsmybatisliquibase.security.JwtTokenProvider;
import com.learning.emsmybatisliquibase.service.EmployeeRoleService;
import com.learning.emsmybatisliquibase.service.EmployeeService;
import com.learning.emsmybatisliquibase.service.PasswordService;
import com.learning.emsmybatisliquibase.service.ProfileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    EmployeeService employeeService;

    @Mock
    ProfileService profileService;

    @Mock
    PasswordService passwordService;

    @Mock
    private PasswordDao passwordDao;

    @Mock
    EmployeeRoleService employeeRoleService;

    @Test
    void testLogin_ValidCredentials_ReturnsTokenAndRoles() {
        UUID employeeUuid = UUID.randomUUID();
        String email = "test@example.com";
        String password = "password123";
        String expectedRole = "EMPLOYEE";
        String expectedToken = "testToken";

        Employee employee = new Employee();
        employee.setUuid(employeeUuid);
        employee.setEmail(email);
        employee.setIsManager(Boolean.TRUE);

        Profile profile = new Profile();
        profile.setEmployeeUuid(employeeUuid);
        profile.setProfileStatus(ProfileStatus.ACTIVE);

        List<Password> passwords = List.of(
                Password.builder()
                        .employeeUuid(employeeUuid)
                        .password(passwordEncoder.encode(password))
                        .status(PasswordStatus.ACTIVE)
                        .build()
        );

        when(passwordEncoder.encode(password)).thenReturn(any());
        when(employeeService.getByEmail(email)).thenReturn(employee);
        when(profileService.getByEmployeeUuid(employeeUuid)).thenReturn(profile);
        when(passwordDao.getByEmployeeUuidAndStatus(employeeUuid, PasswordStatus.ACTIVE)).thenReturn(passwords);
        when(passwordEncoder.matches(password, passwords.get(0).getPassword())).thenReturn(true);
        when(jwtTokenProvider.generateToken(any())).thenReturn(expectedToken);
        when(employeeRoleService.getRolesByEmployeeUuid(employee.getUuid())).thenReturn(List.of(RoleType.EMPLOYEE, RoleType.MANAGER));

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));

        JwtAuthResponseDto result = authService.login(new LoginDto(email, password));

        assertEquals(expectedToken, result.getAccessToken());
        assertEquals(expectedRole, result.getRoles().get(0));
        assertTrue(result.getRoles().contains("MANAGER"));

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(employeeService, times(1)).getByEmail(email);
        verify(profileService, times(1)).getByEmployeeUuid(employeeUuid);
        verify(passwordDao, times(1)).getByEmployeeUuidAndStatus(employeeUuid, PasswordStatus.ACTIVE);
        verify(jwtTokenProvider, times(1)).generateToken(any());
        verify(employeeRoleService, times(1)).getRolesByEmployeeUuid(employee.getUuid());
    }

    @Test
    void testLogin_InvalidCredentials_ThrowsInvalidInputException() {
        UUID employeeUuid = UUID.randomUUID();
        String email = "test@example.com";
        String password = "wrongPassword";

        Employee employee = new Employee();
        employee.setUuid(employeeUuid);
        employee.setEmail(email);

        Profile profile = new Profile();
        profile.setEmployeeUuid(employeeUuid);
        profile.setProfileStatus(ProfileStatus.ACTIVE);

        List<Password> passwords = new ArrayList<>();
        passwords.add(Password.builder()
                .employeeUuid(employeeUuid)
                .password(passwordEncoder.encode(password))
                .status(PasswordStatus.ACTIVE)
                .build());
        when(employeeService.getByEmail(anyString())).thenReturn(employee);
        when(profileService.getByEmployeeUuid(employeeUuid)).thenReturn(profile);
        when(passwordDao.getByEmployeeUuidAndStatus(employeeUuid, PasswordStatus.ACTIVE)).thenReturn(passwords);
        assertDoesNotThrow(() -> passwordService.update(passwords.get(0)));

        assertThrows(InvalidInputException.class, () ->
                authService.login(new LoginDto(email, password)));
    }

    @Test
    void testIsCurrentUser_UserIdNull() {
        Boolean result = authService.isCurrentUser(null);
        assertFalse(result);
    }

    @Test
    void testIsCurrentUserSuccess() {
        UUID employeeUuid = UUID.randomUUID();
        Profile profile = Profile.builder()
                .employeeUuid(employeeUuid)
                .profileStatus(ProfileStatus.ACTIVE)
                .build();

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(profileService.getByEmployeeUuid(any(UUID.class))).thenReturn(profile);
        when(authentication.getName()).thenReturn(employeeUuid.toString());

        Boolean result = authService.isCurrentUser(employeeUuid);
        assertTrue(result);
        assertEquals(employeeUuid, profile.getEmployeeUuid());
    }

    @Test
    void testIsCurrentUser_ProfileNull() {
        UUID employeeUuid = UUID.randomUUID();
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(profileService.getByEmployeeUuid(any(UUID.class))).thenReturn(null);
        when(authentication.getName()).thenReturn(employeeUuid.toString());

        Boolean result = authService.isCurrentUser(employeeUuid);

        assertFalse(result);
    }

    @Test
    void testIsEmployeeManager_Success() {
        UUID employeeUuid = UUID.randomUUID(), managerUuid = UUID.randomUUID();

        Employee employee = Employee.builder()
                .uuid(employeeUuid)
                .managerUuid(managerUuid)
                .build();
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(managerUuid.toString());

        when(employeeService.getById(employeeUuid)).thenReturn(employee);

        Boolean result = authService.isEmployeeManager(employeeUuid);

        assertTrue(result);
    }

    @Test
    void testIsEmployeeManager_Expect() {
        UUID employeeUuid = UUID.randomUUID(), managerUuid = UUID.randomUUID();

        Employee employee = Employee.builder()
                .uuid(employeeUuid)
                .managerUuid(UUID.randomUUID())
                .build();
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(managerUuid.toString());

        when(employeeService.getById(employeeUuid)).thenReturn(employee);

        Boolean result = authService.isEmployeeManager(employeeUuid);

        assertFalse(result);
    }
}