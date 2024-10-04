package com.learning.emsmybatisliquibase.service.impl;

import com.learning.emsmybatisliquibase.dao.EmployeeDao;
import com.learning.emsmybatisliquibase.dao.EmployeeRoleDao;
import com.learning.emsmybatisliquibase.dao.PasswordDao;
import com.learning.emsmybatisliquibase.dao.ProfileDao;
import com.learning.emsmybatisliquibase.dto.JwtAuthResponseDto;
import com.learning.emsmybatisliquibase.dto.LoginDto;
import com.learning.emsmybatisliquibase.entity.*;
import com.learning.emsmybatisliquibase.exception.InvalidInputException;
import com.learning.emsmybatisliquibase.exception.NotFoundException;
import com.learning.emsmybatisliquibase.security.JwtTokenProvider;
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
    private EmployeeDao employeeDao;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private EmployeeRoleDao employeeRoleDao;

    @Mock
    private ProfileDao profileDao;

    @Mock
    private PasswordDao passwordDao;

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

        List<Password> passwords = new ArrayList<>();
        passwords.add(Password.builder()
                .employeeUuid(employeeUuid)
                .password(passwordEncoder.encode(password))
                .status(PasswordStatus.ACTIVE)
                .build());

        List<EmployeeRole> employeeRoles = new ArrayList<>();
        employeeRoles.add(EmployeeRole.builder()
                .employeeUuid(employeeUuid)
                .role(RoleType.EMPLOYEE)
                .build());
        employeeRoles.add(EmployeeRole.builder()
                .employeeUuid(employeeUuid)
                .role(RoleType.MANAGER)
                .build());

        when(employeeDao.getByEmail(anyString())).thenReturn(employee);
        when(profileDao.get(employeeUuid)).thenReturn(profile);
        when(passwordDao.getByEmployeeUuidAndStatus(employeeUuid, PasswordStatus.ACTIVE)).thenReturn(passwords);
        when(passwordEncoder.matches(password, passwords.get(0).getPassword())).thenReturn(true);
        when(jwtTokenProvider.generateToken(any())).thenReturn(expectedToken);
        when(employeeRoleDao.getByEmployeeUuid(employee.getUuid())).thenReturn(employeeRoles);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));

        JwtAuthResponseDto result = authService.login(new LoginDto(email, password));

        assertEquals(expectedToken, result.getAccessToken());
        assertEquals(expectedRole, result.getRoles().get(0));
        assertTrue(result.getRoles().contains("MANAGER"));

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(employeeDao, times(1)).getByEmail(email);
        verify(passwordDao, times(1)).getByEmployeeUuidAndStatus(employeeUuid, PasswordStatus.ACTIVE);
        verify(jwtTokenProvider, times(1)).generateToken(any());
        verify(employeeRoleDao, times(1)).getByEmployeeUuid(employee.getUuid());
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
        when(employeeDao.getByEmail(anyString())).thenReturn(employee);
        when(profileDao.get(employeeUuid)).thenReturn(profile);
        when(passwordDao.getByEmployeeUuidAndStatus(employeeUuid, PasswordStatus.ACTIVE)).thenReturn(passwords);
        when(passwordEncoder.matches(password, passwords.get(0).getPassword())).thenReturn(false);
        when(passwordDao.update(passwords.get(0))).thenReturn(1);

        assertThrows(InvalidInputException.class, () ->
                authService.login(new LoginDto(email, password)));
    }

    @Test
    void testLogin_EmployeeNotFound_ThrowsNotFoundException() {
        String email = "test@example.com";

        when(employeeDao.getByEmail(email)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> authService.login(new LoginDto(email, "password123")));
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

        when(profileDao.get(any(UUID.class))).thenReturn(profile);
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

        when(profileDao.get(any(UUID.class))).thenReturn(null);
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

        when(employeeDao.get(employeeUuid)).thenReturn(employee);

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

        when(employeeDao.get(employeeUuid)).thenReturn(employee);

        Boolean result = authService.isEmployeeManager(employeeUuid);

        assertFalse(result);
    }
}