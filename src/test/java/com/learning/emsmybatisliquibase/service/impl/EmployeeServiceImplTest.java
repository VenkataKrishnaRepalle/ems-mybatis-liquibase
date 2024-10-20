package com.learning.emsmybatisliquibase.service.impl;

import com.learning.emsmybatisliquibase.dao.DepartmentDao;
import com.learning.emsmybatisliquibase.dao.EmployeeDao;
import com.learning.emsmybatisliquibase.dao.ProfileDao;
import com.learning.emsmybatisliquibase.dto.AddEmployeeDto;
import com.learning.emsmybatisliquibase.dto.AddEmployeeResponseDto;
import com.learning.emsmybatisliquibase.entity.Department;
import com.learning.emsmybatisliquibase.entity.Employee;
import com.learning.emsmybatisliquibase.entity.Gender;
import com.learning.emsmybatisliquibase.entity.Profile;
import com.learning.emsmybatisliquibase.exception.NotFoundException;
import com.learning.emsmybatisliquibase.mapper.EmployeeMapper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static com.learning.emsmybatisliquibase.exception.errorcodes.EmployeeErrorCodes.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeDao employeeDao;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private EmployeeMapper employeeMapper;

    @Mock
    private DepartmentDao departmentDao;

    @Mock
    private ProfileDao profileDao;

    Employee employee;

    @BeforeEach
    void setUp() {
        employee = Employee.builder()
                .uuid(UUID.randomUUID())
                .firstName("testFirstName")
                .lastName("testLastName")
                .email("test@gmail.com")
                .isManager(Boolean.FALSE)
                .build();

        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetByIdExpect() {
        lenient().when(employeeDao.get(any(UUID.class))).thenReturn(null);

        assertThrows(NotFoundException.class, () ->
                employeeService.getById(UUID.randomUUID()));
    }

//    @Test
//    void testGetByIdSuccess() {
//        when(employeeDao.get(employee.getUuid())).thenReturn(employee);
//
//        Employee result = employeeService.getById(employee.getUuid());
//        assertEquals(employee, result);
//
//        verify(employeeDao, times(1)).get(eq(employee.getUuid()));
//    }

//    @Test
//    void testIsManagerSuccess() {
//        UUID managerUuid = UUID.randomUUID();
//        Employee manager = Employee.builder()
//                .uuid(managerUuid)
//                .isManager(Boolean.TRUE)
//                .build();
//        when(employeeDao.get(any(UUID.class))).thenReturn(manager);
//
//        assertDoesNotThrow(() -> employeeService.isManager(managerUuid));
//    }

//    @Test
//    void testIsManagerExpect() {
//        when(employeeDao.get(any(UUID.class))).thenReturn(employee);
//
//        NotFoundException exception = assertThrows(NotFoundException.class, () ->
//                employeeService.isManager(employee.getUuid()));
//
//        assertEquals(MANAGER_ACCESS_NOT_FOUND.code(), exception.getErrorCode());
//        assertEquals("User is not a Manager", exception.getDynamicValue());
//    }

//    @Test
//    void testAddSuccess() throws MessagingException, UnsupportedEncodingException {
//        Employee manager = Employee.builder()
//                .uuid(UUID.randomUUID())
//                .isManager(Boolean.TRUE)
//                .build();
//        AddEmployeeDto employeeDto = AddEmployeeDto.builder()
//                .firstName("testFirstName")
//                .lastName("testLastName")
//                .email("test@gmail.com")
//                .gender(Gender.MALE)
//                .isManager(String.valueOf(Boolean.FALSE))
//                .managerUuid(manager.getUuid())
//                .dateOfBirth(LocalDate.now())
//                .departmentName("Tesco")
//                .build();
//
//        Department department = Department.builder()
//                .uuid(UUID.randomUUID())
//                .name("Tesco")
//                .build();
//
//        Profile profile = Profile.builder()
//                .employeeUuid(employee.getUuid())
//                .build();
//
//        AddEmployeeResponseDto response = AddEmployeeResponseDto.builder()
//                .profile(profile)
//                .department(department)
//                .build();
//
//        when(employeeDao.get(any(UUID.class))).thenReturn(manager);
//        when(employeeDao.getByEmail(anyString())).thenReturn(null);
//        when(employeeMapper.addEmployeeDtoToEmployee(any(AddEmployeeDto.class))).thenReturn(employee);
//        when(departmentDao.getByName(employeeDto.getDepartmentName())).thenReturn(department);
//        when(employeeDao.insert(any(Employee.class))).thenReturn(1);
//        when(profileDao.insert(any(Profile.class))).thenReturn(1);
//        when(employeeMapper.employeeToAddEmployeeResponseDto(any(Employee.class))).thenReturn(response);
//
//        MimeMessage mimeMessage = mock(MimeMessage.class);
//        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
//
//
//        AddEmployeeResponseDto finalResponse = employeeService.add(employeeDto);
//
//        verify(employeeDao, times(4)).get(any(UUID.class));
//        verify(employeeDao, times(1)).getByEmail(anyString());
//        verify(employeeMapper, times(1)).addEmployeeDtoToEmployee(any());
//        verify(departmentDao, times(1)).getByName(anyString());
//        verify(employeeDao, times(1)).insert(any());
//        verify(profileDao, times(1)).insert(any());
//        verify(employeeMapper, times(1)).employeeToAddEmployeeResponseDto(any());
//    }
}