package com.learning.emsmybatisliquibase.bootstrap;

import com.learning.emsmybatisliquibase.dao.*;
import com.learning.emsmybatisliquibase.entity.*;
import com.learning.emsmybatisliquibase.service.CycleService;
import com.learning.emsmybatisliquibase.service.EmployeeCycleService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.Instant;
import java.time.Year;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class Bootstrap implements CommandLineRunner {

    private final EmployeeDao employeeDao;

    private final PasswordDao passwordDao;

    private final ProfileDao profileDao;

    private final DepartmentDao departmentDao;

    private final CycleService cycleService;

    private final EmployeeCycleService employeeCycleService;

    private final EmployeeRoleDao employeeRoleDao;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final CycleDao cycleDao;

    private static final String ADMIN = "admin";

    @Override
    public void run(String... args) {
        if (employeeDao.count() < 6) {
            var employee = Employee.builder()
                    .uuid(UUID.randomUUID())
                    .firstName(ADMIN)
                    .lastName(ADMIN)
                    .email("admin@gmail.com")
                    .phoneNumber("1234567890")
                    .dateOfBirth(Date.valueOf("2000-01-01").toLocalDate())
                    .joiningDate(Date.valueOf("2022-01-01").toLocalDate())
                    .gender(Gender.MALE)
                    .isManager(Boolean.FALSE)
                    .createdTime(Instant.now())
                    .updatedTime(Instant.now())
                    .build();
            employeeDao.insert(employee);

            var password = Password.builder()
                    .uuid(UUID.randomUUID())
                    .employeeUuid(employee.getUuid())
                    .password(passwordEncoder.encode(ADMIN))
                    .status(PasswordStatus.ACTIVE)
                    .createdTime(Instant.now())
                    .updatedTime(Instant.now())
                    .build();
            passwordDao.insert(password);

            var profile = Profile.builder()
                    .employeeUuid(employee.getUuid())
                    .profileStatus(ProfileStatus.ACTIVE)
                    .jobTitle(JobTitleType.CEO)
                    .updatedTime(Instant.now())
                    .build();
            profileDao.insert(profile);

            employeeRoleDao.insert(EmployeeRole.builder()
                    .employeeUuid(employee.getUuid())
                    .role(RoleType.ADMIN)
                    .build());

            var employee1 = Employee.builder()
                    .uuid(UUID.randomUUID())
                    .firstName("venky")
                    .lastName("repalle")
                    .email("rvkrishna13052001@gmail.com")
                    .phoneNumber("9059712824")
                    .dateOfBirth(Date.valueOf("2001-05-13").toLocalDate())
                    .joiningDate(Date.valueOf("2018-07-04").toLocalDate())
                    .gender(Gender.MALE)
                    .isManager(Boolean.TRUE)
                    .createdTime(Instant.now())
                    .updatedTime(Instant.now())
                    .build();
            employeeDao.insert(employee1);

            var password1 = Password.builder()
                    .uuid(UUID.randomUUID())
                    .employeeUuid(employee1.getUuid())
                    .password(passwordEncoder.encode("venky123"))
                    .status(PasswordStatus.ACTIVE)
                    .createdTime(Instant.now())
                    .updatedTime(Instant.now())
                    .build();
            passwordDao.insert(password1);

            var department1 = Department.builder()
                    .uuid(UUID.randomUUID())
                    .name("Tesco")
                    .build();
            departmentDao.insert(department1);

            var profile1 = Profile.builder()
                    .employeeUuid(employee1.getUuid())
                    .jobTitle(JobTitleType.SENIOR_PROJECT_MANAGER)
                    .profileStatus(ProfileStatus.ACTIVE)
                    .departmentUuid(department1.getUuid())
                    .updatedTime(Instant.now())
                    .build();

            profileDao.insert(profile1);

            var employee2 = Employee.builder()
                    .uuid(UUID.randomUUID())
                    .firstName("chandu")
                    .lastName("raya")
                    .email("chandu.raya@gmail.com")
                    .phoneNumber("6305177093")
                    .dateOfBirth(Date.valueOf("2000-11-01").toLocalDate())
                    .joiningDate(Date.valueOf("2019-07-04").toLocalDate())
                    .gender(Gender.MALE)
                    .isManager(Boolean.FALSE)
                    .managerUuid(employee1.getUuid())
                    .createdTime(Instant.now())
                    .updatedTime(Instant.now())
                    .build();
            employeeDao.insert(employee2);

            var password2 = Password.builder()
                    .uuid(UUID.randomUUID())
                    .employeeUuid(employee2.getUuid())
                    .password(passwordEncoder.encode("chandu123"))
                    .status(PasswordStatus.ACTIVE)
                    .createdTime(Instant.now())
                    .updatedTime(Instant.now())
                    .build();
            passwordDao.insert(password2);

            var profile2 = Profile.builder()
                    .employeeUuid(employee2.getUuid())
                    .jobTitle(JobTitleType.ENGINEER_TRAINEE)
                    .profileStatus(ProfileStatus.ACTIVE)
                    .departmentUuid(department1.getUuid())
                    .updatedTime(Instant.now())
                    .build();

            profileDao.insert(profile2);

            var employee3 = Employee.builder()
                    .uuid(UUID.randomUUID())
                    .firstName("sujith")
                    .lastName("bikki")
                    .email("sujith.bikki@gmail.com")
                    .phoneNumber("1234567890")
                    .dateOfBirth(Date.valueOf("2000-05-14").toLocalDate())
                    .joiningDate(Date.valueOf("2020-07-04").toLocalDate())
                    .gender(Gender.MALE)
                    .isManager(Boolean.FALSE)
                    .managerUuid(employee1.getUuid())
                    .createdTime(Instant.now())
                    .updatedTime(Instant.now())
                    .build();
            employeeDao.insert(employee3);

            var password3 = Password.builder()
                    .uuid(UUID.randomUUID())
                    .employeeUuid(employee3.getUuid())
                    .password(passwordEncoder.encode("sujith123"))
                    .status(PasswordStatus.ACTIVE)
                    .createdTime(Instant.now())
                    .updatedTime(Instant.now())
                    .build();
            passwordDao.insert(password3);

            var profile3 = Profile.builder()
                    .employeeUuid(employee3.getUuid())
                    .jobTitle(JobTitleType.SOFTWARE_ENGINEER)
                    .profileStatus(ProfileStatus.ACTIVE)
                    .departmentUuid(department1.getUuid())
                    .updatedTime(Instant.now())
                    .build();
            profileDao.insert(profile3);

            var department2 = Department.builder()
                    .uuid(UUID.randomUUID())
                    .name("Capita")
                    .build();
            departmentDao.insert(department2);

            var employee4 = Employee.builder()
                    .uuid(UUID.randomUUID())
                    .firstName("veeranji")
                    .lastName("katari")
                    .email("veeranji.katari@gmail.com")
                    .phoneNumber("9490903106")
                    .dateOfBirth(Date.valueOf("2000-11-18").toLocalDate())
                    .joiningDate(Date.valueOf("2021-07-04").toLocalDate())
                    .gender(Gender.MALE)
                    .isManager(Boolean.TRUE)
                    .managerUuid(employee1.getUuid())
                    .createdTime(Instant.now())
                    .updatedTime(Instant.now())
                    .build();
            employeeDao.insert(employee4);

            var password4 = Password.builder()
                    .uuid(UUID.randomUUID())
                    .employeeUuid(employee4.getUuid())
                    .password(passwordEncoder.encode("veeranji123"))
                    .status(PasswordStatus.ACTIVE)
                    .createdTime(Instant.now())
                    .updatedTime(Instant.now())
                    .build();
            passwordDao.insert(password4);

            var profile4 = Profile.builder()
                    .employeeUuid(employee4.getUuid())
                    .jobTitle(JobTitleType.PROJECT_MANAGER)
                    .profileStatus(ProfileStatus.ACTIVE)
                    .departmentUuid(department2.getUuid())
                    .updatedTime(Instant.now())
                    .build();
            profileDao.insert(profile4);

            var employee5 = Employee.builder()
                    .uuid(UUID.randomUUID())
                    .firstName("lakshman")
                    .lastName("jampani")
                    .email("lakshman.jampani@gmail.com")
                    .phoneNumber("6303537771")
                    .dateOfBirth(Date.valueOf("2001-02-28").toLocalDate())
                    .joiningDate(Date.valueOf("2022-07-04").toLocalDate())
                    .gender(Gender.MALE)
                    .isManager(Boolean.FALSE)
                    .managerUuid(employee4.getUuid())
                    .createdTime(Instant.now())
                    .updatedTime(Instant.now())
                    .build();
            employeeDao.insert(employee5);

            var password5 = Password.builder()
                    .uuid(UUID.randomUUID())
                    .employeeUuid(employee5.getUuid())
                    .password(passwordEncoder.encode("lakshman123"))
                    .status(PasswordStatus.ACTIVE)
                    .createdTime(Instant.now())
                    .updatedTime(Instant.now())
                    .build();
            passwordDao.insert(password5);

            var profile5 = Profile.builder()
                    .employeeUuid(employee5.getUuid())
                    .jobTitle(JobTitleType.TECHNICAL_LEAD)
                    .profileStatus(ProfileStatus.ACTIVE)
                    .departmentUuid(department2.getUuid())
                    .updatedTime(Instant.now())
                    .build();
            profileDao.insert(profile5);

            var cycle = cycleService.createCycle(Year.now().getValue());
            cycle.setStatus(CycleStatus.STARTED);
            cycleDao.update(cycle);

            employeeCycleService.cycleAssignment(List.of(employee1.getUuid(), employee2.getUuid(), employee3.getUuid(), employee4.getUuid(), employee5.getUuid()));
        }
    }
}
